package com.example.kafka.abstraction.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.Properties;

@Component
public class CertificateManager {

    private static final Logger logger = LoggerFactory.getLogger(CertificateManager.class);

    public Properties createSslProperties(String keystoreLocation, String keystorePassword, 
                                         String keystoreType, String truststoreLocation, 
                                         String truststorePassword, String truststoreType,
                                         String keyPassword, String sslEndpointIdentificationAlgorithm) {
        
        Properties sslProperties = new Properties();
        
        try {
            if (StringUtils.hasText(keystoreLocation)) {
                validateKeystoreFile(keystoreLocation, keystorePassword, keystoreType);
                sslProperties.put("ssl.keystore.location", keystoreLocation);
                sslProperties.put("ssl.keystore.password", keystorePassword);
                sslProperties.put("ssl.keystore.type", keystoreType != null ? keystoreType : "JKS");
                
                if (StringUtils.hasText(keyPassword)) {
                    sslProperties.put("ssl.key.password", keyPassword);
                }
            }
            
            if (StringUtils.hasText(truststoreLocation)) {
                validateTruststoreFile(truststoreLocation, truststorePassword, truststoreType);
                sslProperties.put("ssl.truststore.location", truststoreLocation);
                sslProperties.put("ssl.truststore.password", truststorePassword);
                sslProperties.put("ssl.truststore.type", truststoreType != null ? truststoreType : "JKS");
            }
            
            sslProperties.put("security.protocol", "SSL");
            sslProperties.put("ssl.endpoint.identification.algorithm", 
                             sslEndpointIdentificationAlgorithm != null ? sslEndpointIdentificationAlgorithm : "");
            
            logger.info("SSL properties configured successfully");
            return sslProperties;
            
        } catch (Exception e) {
            logger.error("Error creating SSL properties", e);
            throw new CertificateException("Failed to create SSL properties", e);
        }
    }

    public KeyManager[] createKeyManagers(String keystoreLocation, String keystorePassword, 
                                          String keystoreType, String keyPassword) {
        try {
            if (!StringUtils.hasText(keystoreLocation)) {
                return null;
            }

            KeyStore keyStore = loadKeyStore(keystoreLocation, keystorePassword, keystoreType);
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            
            char[] keyPasswordChars = StringUtils.hasText(keyPassword) ? 
                keyPassword.toCharArray() : keystorePassword.toCharArray();
            
            keyManagerFactory.init(keyStore, keyPasswordChars);
            
            logger.info("Key managers created successfully from keystore: {}", keystoreLocation);
            return keyManagerFactory.getKeyManagers();
            
        } catch (Exception e) {
            logger.error("Error creating key managers from keystore: {}", keystoreLocation, e);
            throw new CertificateException("Failed to create key managers", e);
        }
    }

    public TrustManager[] createTrustManagers(String truststoreLocation, String truststorePassword, 
                                             String truststoreType) {
        try {
            if (!StringUtils.hasText(truststoreLocation)) {
                return null;
            }

            KeyStore trustStore = loadKeyStore(truststoreLocation, truststorePassword, truststoreType);
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustStore);
            
            logger.info("Trust managers created successfully from truststore: {}", truststoreLocation);
            return trustManagerFactory.getTrustManagers();
            
        } catch (Exception e) {
            logger.error("Error creating trust managers from truststore: {}", truststoreLocation, e);
            throw new CertificateException("Failed to create trust managers", e);
        }
    }

    private KeyStore loadKeyStore(String location, String password, String type) throws Exception {
        KeyStore keyStore = KeyStore.getInstance(type != null ? type : "JKS");
        
        try (InputStream inputStream = new FileInputStream(location)) {
            keyStore.load(inputStream, password != null ? password.toCharArray() : null);
        }
        
        return keyStore;
    }

    private void validateKeystoreFile(String keystoreLocation, String keystorePassword, String keystoreType) {
        try {
            loadKeyStore(keystoreLocation, keystorePassword, keystoreType);
            logger.debug("Keystore validation successful: {}", keystoreLocation);
        } catch (Exception e) {
            logger.error("Keystore validation failed: {}", keystoreLocation, e);
            throw new CertificateException("Invalid keystore configuration", e);
        }
    }

    private void validateTruststoreFile(String truststoreLocation, String truststorePassword, String truststoreType) {
        try {
            loadKeyStore(truststoreLocation, truststorePassword, truststoreType);
            logger.debug("Truststore validation successful: {}", truststoreLocation);
        } catch (Exception e) {
            logger.error("Truststore validation failed: {}", truststoreLocation, e);
            throw new CertificateException("Invalid truststore configuration", e);
        }
    }

    public boolean isSslConfigurationValid(String keystoreLocation, String truststoreLocation) {
        if (!StringUtils.hasText(keystoreLocation) && !StringUtils.hasText(truststoreLocation)) {
            logger.warn("No SSL configuration provided - both keystore and truststore locations are empty");
            return false;
        }
        
        return true;
    }
}