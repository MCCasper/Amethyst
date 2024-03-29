package wtf.casper.amethyst.core.storage.impl.fstorage;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.SneakyThrows;
import wtf.casper.amethyst.core.AmethystCore;
import wtf.casper.amethyst.core.cache.Cache;
import wtf.casper.amethyst.core.cache.CaffeineCache;
import wtf.casper.amethyst.core.storage.ConstructableValue;
import wtf.casper.amethyst.core.storage.FieldStorage;
import wtf.casper.amethyst.core.storage.id.StorageSerialized;
import wtf.casper.amethyst.core.storage.id.Transient;
import wtf.casper.amethyst.core.storage.id.exceptions.IdNotFoundException;
import wtf.casper.amethyst.core.storage.id.utils.IdUtils;
import wtf.casper.amethyst.core.unsafe.UnsafeConsumer;
import wtf.casper.amethyst.core.utils.AmethystLogger;
import wtf.casper.amethyst.core.utils.ReflectionUtil;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public abstract class SQLiteFStorage<K, V> implements ConstructableValue<K, V>, FieldStorage<K, V> {

    private final Connection connection;
    private final Class<K> keyClass;
    private final Class<V> valueClass;
    private final String table;
    private Cache<K, V> cache = new CaffeineCache<>(Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build());

    @SneakyThrows
    public SQLiteFStorage(final Class<K> keyClass, final Class<V> valueClass, final File file, String table) {
        this.keyClass = keyClass;
        this.valueClass = valueClass;
        this.table = table;
        this.connection = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
        this.execute(createTableFromObject());
        this.scanForMissingColumns();
    }

    @SneakyThrows
    public SQLiteFStorage(final Class<K> keyClass, final Class<V> valueClass, final String table, final String connection) {
        this.keyClass = keyClass;
        this.valueClass = valueClass;
        this.table = table;
        this.connection = DriverManager.getConnection(connection);
        this.execute(createTableFromObject());
        this.scanForMissingColumns();
    }

    @Override
    public Cache<K, V> cache() {
        return this.cache;
    }

    @Override
    public void cache(Cache<K, V> cache) {
        this.cache = cache;
    }

    @SneakyThrows
    public CompletableFuture<Collection<V>> get(final String field, Object value, FilterType filterType, SortingType sortingType) {
        return CompletableFuture.supplyAsync(() -> {
            final List<V> values = new ArrayList<>();
            if (!filterType.isApplicable(value.getClass())) {
                AmethystLogger.error("Filter type " + filterType.name() + " is not applicable to " + value.getClass().getSimpleName());
                return values;
            }

            switch (filterType) {
                case EQUALS -> {
                    try (final PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + this.table + " WHERE " + field + " = ?")) {
                        if (value instanceof UUID) {
                            statement.setString(1, value.toString());
                        } else {
                            statement.setObject(1, value);
                        }

                        final ResultSet resultSet = statement.executeQuery();
                        while (resultSet.next()) {
                            values.add(this.construct(resultSet));
                        }
                    } catch (final SQLException e) {
                        e.printStackTrace();
                    }
                }
                case CONTAINS -> {
                    try (final PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + this.table + " WHERE " + field + " LIKE ?")) {
                        statement.setObject(1, "%" + value + "%");
                        final ResultSet resultSet = statement.executeQuery();
                        while (resultSet.next()) {
                            values.add(this.construct(resultSet));
                        }
                    } catch (final SQLException e) {
                        e.printStackTrace();
                    }
                }
                case STARTS_WITH -> {
                    try (final PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + this.table + " WHERE " + field + " LIKE ?")) {
                        statement.setObject(1, value + "%");
                        final ResultSet resultSet = statement.executeQuery();
                        while (resultSet.next()) {
                            values.add(this.construct(resultSet));
                        }
                    } catch (final SQLException e) {
                        e.printStackTrace();
                    }
                }
                case ENDS_WITH -> {
                    try (final PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + this.table + " WHERE " + field + " LIKE ?")) {
                        statement.setObject(1, "%" + value);
                        final ResultSet resultSet = statement.executeQuery();
                        while (resultSet.next()) {
                            values.add(this.construct(resultSet));
                        }
                    } catch (final SQLException e) {
                        e.printStackTrace();
                    }
                }
                case GREATER_THAN -> {
                    try (final PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + this.table + " WHERE " + field + " > ?")) {
                        statement.setObject(1, value);
                        final ResultSet resultSet = statement.executeQuery();
                        while (resultSet.next()) {
                            values.add(this.construct(resultSet));
                        }
                    } catch (final SQLException e) {
                        e.printStackTrace();
                    }
                }
                case LESS_THAN -> {
                    try (final PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + this.table + " WHERE " + field + " < ?")) {
                        statement.setObject(1, value);
                        final ResultSet resultSet = statement.executeQuery();
                        while (resultSet.next()) {
                            values.add(this.construct(resultSet));
                        }
                    } catch (final SQLException e) {
                        e.printStackTrace();
                    }
                }
                case GREATER_THAN_OR_EQUAL_TO -> {
                    try (final PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + this.table + " WHERE " + field + " >= ?")) {
                        statement.setObject(1, value);
                        final ResultSet resultSet = statement.executeQuery();
                        while (resultSet.next()) {
                            values.add(this.construct(resultSet));
                        }
                    } catch (final SQLException e) {
                        e.printStackTrace();
                    }
                }
                case LESS_THAN_OR_EQUAL_TO -> {
                    try (final PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + this.table + " WHERE " + field + " <= ?")) {
                        statement.setObject(1, value);
                        final ResultSet resultSet = statement.executeQuery();
                        while (resultSet.next()) {
                            values.add(this.construct(resultSet));
                        }
                    } catch (final SQLException e) {
                        e.printStackTrace();
                    }
                }
                case IN -> {
                    try (final PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + this.table + " WHERE " + field + " IN (?)")) {
                        statement.setObject(1, value);
                        final ResultSet resultSet = statement.executeQuery();
                        while (resultSet.next()) {
                            values.add(this.construct(resultSet));
                        }
                    } catch (final SQLException e) {
                        e.printStackTrace();
                    }
                }
                case NOT_EQUALS -> {
                    try (final PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + this.table + " WHERE " + field + " != ?")) {
                        if (value instanceof UUID) {
                            statement.setString(1, value.toString());
                        } else {
                            statement.setObject(1, value);
                        }
                        final ResultSet resultSet = statement.executeQuery();
                        while (resultSet.next()) {
                            values.add(this.construct(resultSet));
                        }
                    } catch (final SQLException e) {
                        e.printStackTrace();
                    }
                }
                case NOT_CONTAINS -> {
                    try (final PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + this.table + " WHERE " + field + " NOT LIKE ?")) {
                        statement.setObject(1, "%" + value + "%");
                        final ResultSet resultSet = statement.executeQuery();
                        while (resultSet.next()) {
                            values.add(this.construct(resultSet));
                        }
                    } catch (final SQLException e) {
                        e.printStackTrace();
                    }
                }
                case NOT_STARTS_WITH -> {
                    try (final PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + this.table + " WHERE " + field + " NOT LIKE ?")) {
                        statement.setObject(1, value + "%");
                        final ResultSet resultSet = statement.executeQuery();
                        while (resultSet.next()) {
                            values.add(this.construct(resultSet));
                        }
                    } catch (final SQLException e) {
                        e.printStackTrace();
                    }
                }
                case NOT_ENDS_WITH -> {
                    try (final PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + this.table + " WHERE " + field + " NOT LIKE ?")) {
                        statement.setObject(1, "%" + value);
                        final ResultSet resultSet = statement.executeQuery();
                        while (resultSet.next()) {
                            values.add(this.construct(resultSet));
                        }
                    } catch (final SQLException e) {
                        e.printStackTrace();
                    }
                }
                case NOT_IN -> {
                    try (final PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + this.table + " WHERE " + field + " NOT IN (?)")) {
                        statement.setObject(1, value);
                        final ResultSet resultSet = statement.executeQuery();
                        while (resultSet.next()) {
                            values.add(this.construct(resultSet));
                        }
                    } catch (final SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            return values;
        });
    }

    @Override
    public CompletableFuture<V> get(K key) {
        if (cache.getIfPresent(key) != null) {
            return CompletableFuture.completedFuture(cache.getIfPresent(key));
        }
        return getFirst(IdUtils.getIdName(this.valueClass), key);
    }

    @Override
    public CompletableFuture<V> getFirst(String field, Object value, FilterType filterType) {
        return CompletableFuture.supplyAsync(() -> {
            return this.get(field, value, filterType, SortingType.NONE).join().stream().findFirst().orElse(null);
        });
    }

    @Override
    public CompletableFuture<Void> save(final V value) {
        return CompletableFuture.runAsync(() -> {
            Object id = IdUtils.getId(valueClass, value);

            if (id == null) {
                return;
            }

            this.cache.put((K) id, value);
            String values = this.getValues(value);
            this.execute("INSERT OR REPLACE INTO " + this.table + " (" + this.getColumns() + ") VALUES (" + values + ");");
        });
    }

    @Override
    public CompletableFuture<Void> remove(final V value) {
        return CompletableFuture.runAsync(() -> {
            Field idField;
            try {
                idField = IdUtils.getIdField(valueClass);
            } catch (IdNotFoundException e) {
                throw new RuntimeException(e);
            }
            String field = idField.getName();
            this.cache.invalidate((K) IdUtils.getId(this.valueClass, value));
            this.execute("DELETE FROM " + this.table + " WHERE " + field + " = ?;", statement -> {
                statement.setString(1, IdUtils.getId(this.valueClass, value).toString());
            });
        });
    }

    @Override
    @SneakyThrows
    public CompletableFuture<Void> write() {
        return CompletableFuture.runAsync(() -> {
        });
    }

    @Override
    public CompletableFuture<Void> close() {
        return CompletableFuture.runAsync(() -> {
            try {
                this.connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<Collection<V>> allValues() {
        return CompletableFuture.supplyAsync(() -> {
            final List<V> values = new ArrayList<>();
            try (final PreparedStatement statement = this.connection.prepareStatement("SELECT * FROM " + this.table)) {
                final ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    values.add(this.construct(resultSet));
                }
            } catch (final SQLException e) {
                e.printStackTrace();
            }
            return values;
        });
    }

    private CompletableFuture<ResultSet> query(final String query, final UnsafeConsumer<PreparedStatement> statement, final UnsafeConsumer<ResultSet> result) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return this.connection.prepareStatement(query);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }).whenCompleteAsync((prepared, exception) -> {
            if (exception != null) {
                exception.printStackTrace();
                return;
            }
            statement.accept(prepared);
        }).thenApply(prepared -> {
            try {
                return prepared.executeQuery();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }).whenCompleteAsync((set, exception) -> {
            if (exception != null) {
                exception.printStackTrace();
                return;
            }
            result.accept(set);
        }).toCompletableFuture();
    }

    private CompletableFuture<ResultSet> query(final String query, final UnsafeConsumer<ResultSet> result) {
        return this.query(query, statement -> {
        }, result);
    }

    private void execute(final String statement) {
        this.execute(statement, ps -> {
        });
    }

    private void execute(String statement, final UnsafeConsumer<PreparedStatement> consumer) {
        try {
            if (this.connection.isClosed()) {
                AmethystLogger.log("Connection is closed, cannot execute statement: " + statement);
                return;
            }
            try (final PreparedStatement prepared = this.connection.prepareStatement(statement)) {
                consumer.accept(prepared);
                prepared.executeUpdate();
            } catch (final SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void addColumn(final String column, final String type) {
        this.execute("ALTER TABLE " + this.table + " ADD " + column + " " + type + ";");
    }

    /**
     * Will scan the class for fields and add them to the database if they don't exist
     */
    private void scanForMissingColumns() {
        try (final PreparedStatement statement = this.connection.prepareStatement("SELECT * FROM " + this.table)) {
            final ResultSetMetaData metaData = statement.getMetaData();
            final int columnCount = metaData.getColumnCount();
            final List<String> columns = new ArrayList<>();
            for (int i = 1; i <= columnCount; i++) {
                columns.add(metaData.getColumnName(i));
            }

            final List<Field> fields = Arrays.stream(this.valueClass.getDeclaredFields())
                    .filter(field -> !field.isAnnotationPresent(Transient.class))
                    .filter(field -> !Modifier.isTransient(field.getModifiers()))
                    .toList();

            for (final Field field : fields) {
                final String name = field.getName();
                if (!columns.contains(name)) {
                    this.addColumn(name, this.getType(field));
                }
            }
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generate an SQL Script to create the table based on the class
     * */
    private String createTableFromObject() {
        final StringBuilder builder = new StringBuilder();

        List<Field> fields = Arrays.stream(this.valueClass.getDeclaredFields())
                .filter(field -> !field.isAnnotationPresent(Transient.class))
                .filter(field -> !Modifier.isTransient(field.getModifiers()))
                .toList();

        if (fields.size() == 0) {
            return "";
        }

        builder.append("CREATE TABLE IF NOT EXISTS ").append(this.table).append(" (");

        String idName = IdUtils.getIdName(valueClass);

        int index = 0;
        for (Field declaredField : fields) {

            final String name = declaredField.getName();
            String type = this.getType(declaredField);

            builder.append("`").append(name).append("`").append(" ").append(type);
            if (name.equals(idName)) {
                builder.append(" PRIMARY KEY");
            }

            index++;

            if (index != fields.size()) {
                builder.append(", ");
            }

        }
        builder.append(")");
        return builder.toString();
    }

    /**
     * This takes an SQL Result Set and parses it into an object
     */
    @SneakyThrows
    private V construct(final ResultSet resultSet) {
        final V value = constructValue();
        final Field[] declaredFields = this.valueClass.getDeclaredFields();

        for (Field declaredField : declaredFields) {
            if (declaredField.isAnnotationPresent(Transient.class)) {
                continue;
            }
            if (declaredField.isAnnotationPresent(StorageSerialized.class)) {
                final String name = declaredField.getName();
                final String string = resultSet.getString(name);
                final Object object = AmethystCore.getGson().fromJson(string, declaredField.getType());
                declaredField.setAccessible(true);
                declaredField.set(value, object);
                continue;
            }

            final String name = declaredField.getName();
            final Object object = resultSet.getObject(name);

            if (declaredField.getType() == UUID.class && object instanceof String) {
                ReflectionUtil.setPrivateField(value, name, UUID.fromString((String) object));
                continue;
            }

            ReflectionUtil.setPrivateField(value, name, object);
        }

        return value;
    }

    /**
     * Generates an SQL String for the columns associated with a value class.
     * */
    private String getColumns() {
        final StringBuilder builder = new StringBuilder();
        for (final Field field : this.valueClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Transient.class)) {
                continue;
            }
            builder.append("`").append(field.getName()).append("`").append(",");
        }
        return builder.substring(0, builder.length() - 1);
    }


    /**
     * Converts a Java class to an SQL type.
     * */
    private String getType(Field field) {
        if (field.isAnnotationPresent(StorageSerialized.class)) {
            return "VARCHAR(255)";
        }
        return switch (field.getType().getName()) {
            case "java.lang.String" -> "VARCHAR(255)";
            case "java.lang.Integer", "int" -> "INT";
            case "java.lang.Long", "long" -> "BIGINT";
            case "java.lang.Boolean", "boolean" -> "BOOLEAN";
            case "java.lang.Double", "double" -> "DOUBLE";
            case "java.lang.Float", "float" -> "FLOAT";
            case "java.lang.Short", "short" -> "SMALLINT";
            case "java.lang.Byte", "byte" -> "TINYINT";
            case "java.lang.Character", "char" -> "CHAR";
            case "java.util.UUID" -> "VARCHAR(36)";
            default -> "VARCHAR(255)";
        };
    }

    /**
     * Generates an SQL String for inserting a value into the database.
     * */
    private String getValues(V value) {
        final StringBuilder builder = new StringBuilder();
        int i = 0;

        List<Field> fields = Arrays.stream(this.valueClass.getDeclaredFields())
                .filter(field -> !field.isAnnotationPresent(Transient.class))
                .filter(field -> !Modifier.isTransient(field.getModifiers()))
                .toList();

        for (final Field field : fields) {
            if (field.isAnnotationPresent(StorageSerialized.class)) {
                builder.append("'").append(AmethystCore.getGson().toJson(ReflectionUtil.getPrivateField(value, field.getName()))).append("'");
            } else {
                boolean shouldHaveQuotes = shouldHaveQuotes(ReflectionUtil.getPrivateField(value, field.getName()));
                if (shouldHaveQuotes) {
                    builder.append("'");
                }
                builder.append(ReflectionUtil.getPrivateField(value, field.getName()));
                if (shouldHaveQuotes) {
                    builder.append("'");
                }
            }
            if (i != fields.size() - 1) {
                builder.append(", ");
            }
            i++;
        }

        return builder.toString();
    }

    private boolean shouldHaveQuotes(Object value) {
        return switch (value.getClass().getName()) {
            case "java.lang.String", "java.util.UUID" -> true;
            default -> false;
        };
    }
}
