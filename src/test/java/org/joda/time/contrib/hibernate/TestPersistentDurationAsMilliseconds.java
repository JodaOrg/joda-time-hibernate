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

import junit.framework.Assert;
import org.hibernate.Session;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.contrib.hibernate.testmodel.SomethingThatLasts;

import java.io.IOException;
import java.sql.SQLException;
import org.hibernate.Transaction;

/**
 * @author gjoseph
 * @author $Author: $ (last edit)
 * @version $Revision: $
 */
@HbmFiles("src/test/java/org/joda/time/contrib/hibernate/testmodel/SomethingThatLastsInMilliseconds.hbm.xml")
public class TestPersistentDurationAsMilliseconds extends HibernateTestCase {

    private final Duration[] durations = new Duration[]{
            Duration.ZERO, new Duration(30), Period.seconds(30).toDurationTo(new DateTime()), Period.months(3).toDurationFrom(new DateTime())
    };

    public void testSimpleStore() throws SQLException, IOException {
        Session session = getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();

        for (int i = 0; i < durations.length; i++) {
            SomethingThatLasts thing = new SomethingThatLasts();
            thing.setId(i);
            thing.setName("test_" + i);
            thing.setTheDuration(durations[i]);
            session.save(thing);
            session.flush();
            session.clear();
        }

        session.flush();
        transaction.commit();
        session.close();

        for (int i = 0; i < durations.length; i++) {
            session = getSessionFactory().openSession();
            SomethingThatLasts lastingThing = (SomethingThatLasts) session.get(SomethingThatLasts.class, new Long(i));

            Assert.assertNotNull(lastingThing);
            Assert.assertEquals(i, lastingThing.getId());
            Assert.assertEquals("test_" + i, lastingThing.getName());
            Assert.assertEquals(durations[i], lastingThing.getTheDuration());

            session.close();
        }

        // printSqlQueryResults("SELECT * FROM lasting");
    }

}
