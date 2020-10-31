/*
 *  Copyright 2001-2012 Stephen Colebourne
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.joda.time.contrib.hibernate;

import java.lang.annotation.Annotation;
import java.sql.SQLException;
import junit.framework.TestCase;

import org.hibernate.SessionFactory;
import org.hibernate.dialect.HSQLDialect;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;

public abstract class HibernateTestCase extends TestCase
{
    private SessionFactory sessionFactory;
    private MetadataSources metadataSources;
    private Metadata metadata;
    
    @Override
    protected void setUp() throws Exception {
        HbmFiles annotation = getClass().getAnnotation(HbmFiles.class);
        if (annotation != null) {
            String[] files = annotation.value();
            
            MetadataSources sources = getMetadataSources();
            createDatabase(getMetadata(files));
        }
    }
    
    protected SessionFactory getSessionFactory()
    {
        if (this.sessionFactory == null)
        {
            sessionFactory = metadata.getSessionFactoryBuilder().build();
        }
        return sessionFactory;
    }

    @Override
    protected void tearDown() throws Exception {
        SchemaExport export = new SchemaExport();
        export.drop(EnumSet.of(TargetType.DATABASE), metadata);
    }

    public Metadata getMetadata() {
        return metadata;
    }

    private void createDatabase(final Metadata metadata) {
        SchemaExport export = new SchemaExport();
        export.create(EnumSet.of(TargetType.DATABASE), metadata);
    }
    
    private MetadataSources getMetadataSources() {
        if (metadataSources == null) {
            final StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder();

            Map<String, String> settings = new HashMap<>();
            settings.put(Environment.DRIVER, "org.hsqldb.jdbcDriver");
            settings.put(Environment.URL, "jdbc:hsqldb:mem:hbmtest" + getClass().getName());
            settings.put(Environment.DIALECT, HSQLDialect.class.getName());
            settings.put(Environment.SHOW_SQL, Boolean.TRUE.toString());

            registryBuilder.applySettings(settings);

            StandardServiceRegistry registry = registryBuilder.build();

            metadataSources = new MetadataSources(registry);
        }
        
        return metadataSources;
    }
    
    private Metadata getMetadata(String... configFiles) {
        if (this.sessionFactory == null) {
            if (metadataSources == null) {
                metadataSources = getMetadataSources();
            }
            for (final String configFile : configFiles) {
                metadataSources.addFile(configFile);
            }
            metadata = metadataSources.getMetadataBuilder().build();
        }
        return metadata;
    }

}
