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

import java.io.IOException;
import java.sql.SQLException;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.joda.time.LocalDate;

@HbmFiles({
    "src/test/java/org/joda/time/contrib/hibernate/event.hbm.xml",
    "src/test/java/org/joda/time/contrib/hibernate/eventTZ.hbm.xml"
})
public class TestPersistentLocalDate extends HibernateTestCase
{
    private final LocalDate[] writeReadTimes = new LocalDate[]
    {
        new LocalDate(2004, 2, 25),
        new LocalDate(1980, 3, 11),
        new LocalDate(1700, 1, 1)
    };

    public void testSimpleStore() throws SQLException, IOException
    {
        SessionFactory factory = getSessionFactory();

        Session session = factory.openSession();
        Transaction transaction = session.beginTransaction();

        for (int i = 0; i<writeReadTimes.length; i++)
        {
            LocalDate writeReadTime = writeReadTimes[i];

            Event event = new Event();
            event.setId(i);
            event.setLocalDate(writeReadTime);

            session.save(event);
            session.flush();
            session.clear();
        }

        session.flush();
        transaction.commit();
        session.close();

        for (int i = 0; i<writeReadTimes.length; i++)
        {
            LocalDate writeReadTime = writeReadTimes[i];

            session = factory.openSession();
            Event eventReread = (Event) session.get(Event.class, new Integer(i));

            assertNotNull("get failed - event#'" + i + "'not found", eventReread);
            assertNotNull("get failed - returned null", eventReread.getLocalDate());

            // we loose the timezone, so we have to normalize both to offset=0
            assertEquals("get failed - returned different time",
                writeReadTime,
                eventReread.getLocalDate());

            session.close();
        }
    }

}
