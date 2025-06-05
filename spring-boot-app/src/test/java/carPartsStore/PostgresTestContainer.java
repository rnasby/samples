package carPartsStore;

//import org.testcontainers.containers.PostgreSQLContainer;

//public class PostgresTestContainer extends PostgreSQLContainer<PostgresTestContainer> {
//    private static PostgresTestContainer container;
//    private static final String IMAGE_VERSION = "postgres:17.5";
//
//    private PostgresTestContainer() {
//        super(IMAGE_VERSION);
//    }
//
//    public static PostgresTestContainer getInstance() {
//        if (container == null) container = new PostgresTestContainer();
//        return container;
//    }
//
//    @Override
//    public void start() {
//        super.start();
//        System.setProperty("DB_URL", container.getJdbcUrl());
//        System.setProperty("DB_USERNAME", container.getUsername());
//        System.setProperty("DB_PASSWORD", container.getPassword());
//    }
//
//    @Override
//    public void stop() {
//        // Do nothing, JVM will shut down
//    }
//}
