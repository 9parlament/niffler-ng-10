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
class TransactionManager {
    public static final int PG_DEFAULT_ISOLATION_LEVEL = Connection.TRANSACTION_READ_COMMITTED;

    static <T> T executeInTransaction(Function<Connection, T> action, Database database) {
        return runInTransaction(action, database, PG_DEFAULT_ISOLATION_LEVEL);
    }

    static <T> T executeRepeatableRead(Function<Connection, T> action, Database database) {
        return runInTransaction(action, database, Connection.TRANSACTION_REPEATABLE_READ);
    }

    static <T> T executeSerializable(Function<Connection, T> action, Database database) {
        return runInTransaction(action, database, Connection.TRANSACTION_SERIALIZABLE);
    }

    static void executeInTransaction(Consumer<Connection> action, Database database) {
        runInTransaction(action, database, PG_DEFAULT_ISOLATION_LEVEL);
    }

    static void executeRepeatableRead(Consumer<Connection> action, Database database) {
        runInTransaction(action, database, Connection.TRANSACTION_REPEATABLE_READ);
    }

    static void executeSerializable(Consumer<Connection> action, Database database) {
        runInTransaction(action, database, Connection.TRANSACTION_SERIALIZABLE);
    }

    @SafeVarargs
    static <T> T executeInXaTransaction(XaFunction<T>... actions) {
        return runInXaTransaction(PG_DEFAULT_ISOLATION_LEVEL, actions);
    }

    @SafeVarargs
    static <T> T executeXaRepeatableRead(XaFunction<T>... actions) {
        return runInXaTransaction(Connection.TRANSACTION_REPEATABLE_READ, actions);
    }

    @SafeVarargs
    static <T> T executeXaSerializable(XaFunction<T>... actions) {
        return runInXaTransaction(Connection.TRANSACTION_SERIALIZABLE, actions);
    }

    static void executeInXaTransaction(XaConsumer... actions) {
        runInXaTransaction(PG_DEFAULT_ISOLATION_LEVEL, actions);
    }

    static void executeXaRepeatableRead(XaConsumer... actions) {
        runInXaTransaction(Connection.TRANSACTION_REPEATABLE_READ, actions);
    }

    static void executeXaSerializable(XaConsumer... actions) {
        runInXaTransaction(Connection.TRANSACTION_SERIALIZABLE, actions);
    }

    record XaFunction<T>(Function<Connection, T> func, Database database) {
    }

    record XaConsumer(Consumer<Connection> func, Database database) {
    }

    private static <T> T runInTransaction(Function<Connection, T> action, Database database, int isolationLevel) {
        Connection connection = null;
        try {
            connection = ConnectionManager.getConnection(database);
            connection.setTransactionIsolation(isolationLevel);
            connection.setAutoCommit(false);
            T result = action.apply(connection);
            connection.commit();
            connection.setAutoCommit(true);
            connection.setTransactionIsolation(PG_DEFAULT_ISOLATION_LEVEL);
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

    private static void runInTransaction(Consumer<Connection> action, Database database, int isolationLevel) {
        Connection connection = null;
        try {
            connection = ConnectionManager.getConnection(database);
            connection.setTransactionIsolation(isolationLevel);
            connection.setAutoCommit(false);
            action.accept(connection);
            connection.commit();
            connection.setAutoCommit(true);
            connection.setTransactionIsolation(PG_DEFAULT_ISOLATION_LEVEL);
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
    private static <T> T runInXaTransaction(int isolationLevel, XaFunction<T>... actions) {
        UserTransaction utx = new UserTransactionImp();
        try {
            utx.begin();
            T result = null;
            Connection connection = null;
            for (XaFunction<T> action : actions) {
                connection = ConnectionManager.getConnection(action.database());
                connection.setTransactionIsolation(isolationLevel);
                result = action.func.apply(connection);
            }
            utx.commit();
            if (Objects.nonNull(connection)) {
                connection.setTransactionIsolation(PG_DEFAULT_ISOLATION_LEVEL);
            }
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

    private static void runInXaTransaction(int isolationLevel, XaConsumer... actions) {
        UserTransaction utx = new UserTransactionImp();
        try {
            utx.begin();
            Connection connection = null;
            for (XaConsumer action : actions) {
                connection = ConnectionManager.getConnection(action.database());
                connection.setTransactionIsolation(isolationLevel);
                action.func.accept(connection);
            }
            utx.commit();
            if (Objects.nonNull(connection)) {
                connection.setTransactionIsolation(PG_DEFAULT_ISOLATION_LEVEL);
            }
        } catch (Exception e) {
            try {
                utx.rollback();
            } catch (SystemException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException("Ошибка при выполнении операции", e);
        }
    }
}
