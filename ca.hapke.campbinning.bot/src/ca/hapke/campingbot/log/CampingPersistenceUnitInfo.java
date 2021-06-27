package ca.hapke.campingbot.log;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;

/**
 * @author Nathan Hapke
 */
public class CampingPersistenceUnitInfo implements PersistenceUnitInfo {

	public static final String JPA_VERSION = "2.1";
	public static final String UNIT_NAME = "campingbot";
	private final List<String> managedClassNames = new ArrayList<>();
	private final List<String> mappingFileNames = new ArrayList<>();
	private DataSource jtaDataSource;
	private DataSource nonJtaDataSource;
	private final Properties properties = new Properties();
	private PersistenceUnitTransactionType transactionType = PersistenceUnitTransactionType.RESOURCE_LOCAL;

	public CampingPersistenceUnitInfo() {

	}

//	public CampingPersistenceUnitInfo setJtaDataSource(DataSource jtaDataSource) {
//		this.jtaDataSource = jtaDataSource;
//		this.nonJtaDataSource = null;
//		transactionType = PersistenceUnitTransactionType.JTA;
//		return this;
//	}
//
//	public CampingPersistenceUnitInfo setNonJtaDataSource(DataSource nonJtaDataSource) {
//		this.nonJtaDataSource = nonJtaDataSource;
//		this.jtaDataSource = null;
//		transactionType = PersistenceUnitTransactionType.RESOURCE_LOCAL;
//		return this;
//	}

	public void add(String className) {
		managedClassNames.add(className);
	}

	@Override
	public boolean excludeUnlistedClasses() {
		return false;
	}

	@Override
	public ClassLoader getClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}

	@Override
	public List<URL> getJarFileUrls() {
//		return Collections.emptyList();
		try {
			return Collections.list(this.getClass().getClassLoader().getResources(""));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public DataSource getJtaDataSource() {
		return jtaDataSource;
	}

	@Override
	public List<String> getManagedClassNames() {
		return managedClassNames;
	}

	@Override
	public List<String> getMappingFileNames() {
		return mappingFileNames;
	}

	@Override
	public ClassLoader getNewTempClassLoader() {
		return null;
	}

	@Override
	public DataSource getNonJtaDataSource() {
		return nonJtaDataSource;
	}

	@Override
	public String getPersistenceProviderClassName() {
		return PersistenceProvider.class.getName();
	}

	@Override
	public String getPersistenceUnitName() {
		return UNIT_NAME;
	}

	@Override
	public URL getPersistenceUnitRootUrl() {
		return null;
	}

	@Override
	public String getPersistenceXMLSchemaVersion() {
		return JPA_VERSION;
	}

	@Override
	public Properties getProperties() {
		return properties;
	}

	@Override
	public SharedCacheMode getSharedCacheMode() {
		return SharedCacheMode.UNSPECIFIED;
	}

	@Override
	public PersistenceUnitTransactionType getTransactionType() {
		return transactionType;
	}

	@Override
	public ValidationMode getValidationMode() {
		return ValidationMode.AUTO;
	}

	@Override
	public void addTransformer(ClassTransformer transformer) {
		// TODO Auto-generated method stub

	}

}
