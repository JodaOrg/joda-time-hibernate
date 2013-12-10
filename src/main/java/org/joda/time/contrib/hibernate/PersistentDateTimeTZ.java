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

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.usertype.UserType;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * Persist {@link org.joda.time.DateTime} via hibernate. The timezone will be
 * stored in an extra column.
 * 
 * @author Mario Ivankovits (mario@ops.co.at)
 */
public class PersistentDateTimeTZ implements UserType, Serializable {

    public static final PersistentDateTimeTZ INSTANCE = new PersistentDateTimeTZ();

    private static final int[] SQL_TYPES = new int[] { Types.TIMESTAMP, Types.VARCHAR, };

    public int[] sqlTypes() {
        return SQL_TYPES;
    }

    public Class returnedClass() {
        return DateTime.class;
    }

    public boolean equals(Object x, Object y) throws HibernateException {
        if (x == y) {
            return true;
        }
        if (x == null || y == null) {
            return false;
        }
        DateTime dtx = (DateTime) x;
        DateTime dty = (DateTime) y;
        return dtx.equals(dty);
    }

    public int hashCode(Object object) throws HibernateException {
        return object.hashCode();
    }

    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
            throws HibernateException, SQLException {
        Object timestamp = StandardBasicTypes.TIMESTAMP.nullSafeGet(rs, names[0], session, owner);
        Object timezone = StandardBasicTypes.STRING.nullSafeGet(rs, names[1], session, owner);
        if (timestamp == null || timezone == null) {
            return null;
        }
        return new DateTime(timestamp, DateTimeZone.forID(timezone.toString()));
    }

    public void nullSafeSet(PreparedStatement statement, Object value, int index, SessionImplementor session)
            throws HibernateException, SQLException {
        if (value == null) {
            StandardBasicTypes.TIMESTAMP.nullSafeSet(statement, null, index, session);
            StandardBasicTypes.STRING.nullSafeSet(statement, null, index + 1, session);
        } else {
            DateTime dt = (DateTime) value;
            StandardBasicTypes.TIMESTAMP.nullSafeSet(statement, dt.toDate(), index, session);
            StandardBasicTypes.STRING.nullSafeSet(statement, dt.getZone().getID(), index + 1, session);
        }
    }

    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    public boolean isMutable() {
        return false;
    }

    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }

    public Object assemble(Serializable cached, Object value) throws HibernateException {
        return cached;
    }

    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }

}
