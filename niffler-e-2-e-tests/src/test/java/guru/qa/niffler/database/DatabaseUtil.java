package guru.qa.niffler.database;

import com.atomikos.icatch.jta.UserTransactionImp;
import jakarta.transaction.SystemException;
import jakarta.transaction.UserTransaction;
import lombok.NoArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
class DatabaseUtil {

    static <T> T runInTransaction(Function<Connection, T> action, Database database) {
        Connection connection = null;
        try {
            connection = ConnectionManager.getConnection(database);
            connection.setAutoCommit(false);
            T result = action.apply(connection);
            connection.commit();
            return result;
        } catch (Exception e) {
            try {
                if (Objects.nonNull(connection)) {
                    connection.rollback();
                    connection.setAutoCommit(true);
                }
            } catch (SQLException ex) {
                e.addSuppressed(ex);
            }
            throw new RuntimeException("Ошибка при выполнении операции", e);
        }
    }

    static void runInTransaction(Consumer<Connection> action, Database database) {
        Connection connection = null;
        try {
            connection = ConnectionManager.getConnection(database);
            connection.setAutoCommit(false);
            action.accept(connection);
            connection.commit();
        } catch (Exception e) {
            try {
                if (Objects.nonNull(connection)) {
                    connection.rollback();
                    connection.setAutoCommit(true);
                }
            } catch (SQLException ex) {
                e.addSuppressed(ex);
            }
            throw new RuntimeException("Ошибка при выполнении операции", e);
        }
    }

    @SafeVarargs
    static <T> T runInXaTransaction(XaFunction<T>... actions) {
        UserTransaction utx = new UserTransactionImp();
        try {
            utx.begin();
            T result = null;
            for (XaFunction<T> action : actions) {
                result = action.func.apply(ConnectionManager.getConnection(action.database()));
            }
            utx.commit();
            return result;
        } catch (Exception e) {
            try {
                utx.rollback();
            } catch (SystemException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException("Ошибка при выполнении операции", e);
        }
    }

    static void runInXaTransaction(XaConsumer... actions) {
        UserTransaction utx = new UserTransactionImp();
        try {
            utx.begin();
            for (XaConsumer action : actions) {
                action.func.accept(ConnectionManager.getConnection(action.database()));
            }
            utx.commit();
        } catch (Exception e) {
            try {
                utx.rollback();
            } catch (SystemException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException("Ошибка при выполнении операции", e);
        }
    }

    record XaFunction<T>(Function<Connection, T> func, Database database) {
    }

    record XaConsumer(Consumer<Connection> func, Database database) {
    }
}
