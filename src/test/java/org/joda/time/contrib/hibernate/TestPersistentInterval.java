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
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.joda.time.DateTime;
import org.joda.time.Interval;

@HbmFiles("src/test/java/org/joda/time/contrib/hibernate/plan.hbm.xml")
public class TestPersistentInterval extends HibernateTestCase
{
    private SessionFactory factory;
    private Session session;
    private DateTime beginDateTime;
    private DateTime endDateTime;
    private Transaction transaction;
    private String intervalQuery;
    
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        factory = getSessionFactory();
        store();
        intervalQuery = "from Plan where :aDate between period.start and period.end";
    }
    
    private void store() throws SQLException, IOException
    {
        openAndBegin();
        
        Plan plan = new Plan(1);
        beginDateTime = new DateTime(1980, 3, 11, 2, 3, 45, 0);
        endDateTime = new DateTime(2004, 2, 25, 17, 3, 45, 760);
        Interval period = new Interval(beginDateTime, endDateTime);
        
        plan.setPeriod(period);
        
        session.save(plan);
        session.flush();
        
        commitAndClose();
    }

    private void openAndBegin()
    {
        session = factory.openSession();
        transaction = session.beginTransaction();
    }
    
    private void commitAndClose() throws IOException
    {
        transaction.commit();
        session.close();
    }
    
    public void testQueryById() throws SQLException, IOException
    {
        openAndBegin();
        Interval persistedPeriod = queryPlan().getPeriod();
        commitAndClose();
        assertPlanPeriod(persistedPeriod);
    }

    private void assertPlanPeriod(Interval period)
    {
        assertEquals(beginDateTime, period.getStart());
        assertEquals(endDateTime, period.getEnd());
    }

    private Plan queryPlan()
    {
        return (Plan) session.get(Plan.class, new Integer(1));
    }
    
    public void testQueryInsideInterval() throws SQLException, IOException
    {
        openAndBegin();
        DateTime includedDateTime = new DateTime(2004, 1, 10, 0, 0, 0, 0);
        Query query = session.createQuery(intervalQuery)
                             .setParameter("aDate", includedDateTime.toDate());
        List queriedPlans = query.list();
        commitAndClose();
        assertEquals(1, queriedPlans.size());
        assertPlanPeriod(((Plan) queriedPlans.get(0)).getPeriod());
    }
    
    public void testQueryOutsideInterval() throws IOException
    {
        openAndBegin();
        DateTime excludedDateTime = new DateTime(2007, 1, 10, 0, 0, 0, 0);
        Query query = session.createQuery(intervalQuery)
                             .setParameter("aDate", excludedDateTime.toDate());
        List queriedPlans = query.list();
        commitAndClose();
        assertEquals(0, queriedPlans.size());
    }

    @Override
    protected void tearDown() throws Exception
    {
        remove();
        super.tearDown();
    }

    private void remove() throws IOException
    {
        openAndBegin();
        session.delete(queryPlan());
        commitAndClose();
    }
}
