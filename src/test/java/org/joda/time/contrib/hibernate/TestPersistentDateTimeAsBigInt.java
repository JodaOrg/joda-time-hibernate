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

import java.io.File;
import java.sql.SQLException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.joda.time.DateTime;

public class TestPersistentDateTimeAsBigInt extends HibernateTestCase
{
    private DateTime[] writeReadTimes = new DateTime[]
    {
        new DateTime(0),
		new DateTime(1000),
		new DateTime(1000000)
    };

    public void testSimpleStore() throws SQLException
    {
        SessionFactory factory = getSessionFactory();

        Session session = factory.openSession();

        for (int i = 0; i<writeReadTimes.length; i++)
        {
            DateTime writeReadTime = writeReadTimes[i];

            ThingWithDateTime thing = new ThingWithDateTime();
            thing.setId(i);
            thing.setDateTime(writeReadTime);

            session.save(thing);
        }

        session.flush();
        session.connection().commit();
        session.close();

        for (int i = 0; i<writeReadTimes.length; i++)
        {
            DateTime writeReadTime = writeReadTimes[i];

            session = factory.openSession();
            ThingWithDateTime thingReread = (ThingWithDateTime)session.get(ThingWithDateTime.class, new Integer(i));

            assertNotNull("get failed - thing#'" + i + "'not found", thingReread);
            assertNotNull("get failed - returned null", thingReread.getDateTime());

			DateTime reReadTime = thingReread.getDateTime();
			if (writeReadTime.getMillis() != reReadTime.getMillis())
			{
				fail("get failed - returned different date. expected " + writeReadTime + " was " + thingReread.getDateTime());
			}
		}

		session.close();
    }

	protected void setupConfiguration(Configuration cfg)
	{
		cfg.addFile(new File("src/test/java/org/joda/time/contrib/hibernate/thingWithDateTimeAsBigInt.hbm.xml"));
	}
}
