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
import org.joda.time.YearMonthDay;

@HbmFiles("src/test/java/org/joda/time/contrib/hibernate/schedule.hbm.xml")
public class TestPersistentYearMonthDay extends HibernateTestCase
{
    private final YearMonthDay[] writeReadTimes = new YearMonthDay[]
    {
        new YearMonthDay(2004, 2, 25),
        new YearMonthDay(1980, 3, 11)
    };

    public void testSimpleStore() throws SQLException, IOException
    {
        SessionFactory factory = getSessionFactory();

        Session session = factory.openSession();
        Transaction transaction = session.beginTransaction();

        for (int i = 0; i<writeReadTimes.length; i++)
        {
            YearMonthDay writeReadTime = writeReadTimes[i];

            Schedule event = new Schedule();
            event.setId(i);
            event.setStartDate(writeReadTime);

            session.save(event);
            session.flush();
            session.clear();
        }

        session.flush();
        transaction.commit();
        session.close();

        for (int i = 0; i<writeReadTimes.length; i++)
        {
            YearMonthDay writeReadTime = writeReadTimes[i];

            session = factory.openSession();
            Schedule eventReread = (Schedule) session.get(Schedule.class, new Integer(i));

            assertNotNull("get failed - event#'" + i + "'not found", eventReread);
            assertNotNull("get failed - returned null", eventReread.getStartDate());

            assertEquals("get failed - returned different date", writeReadTime, eventReread.getStartDate());
        }
        
        session.close();
    }

}
