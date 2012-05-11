/*
 *  Copyright 2001-2009 Stephen Colebourne
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
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.usertype.UserType;
import org.joda.time.Duration;

/**
 * Converts a org.joda.time.Duration to and from Sql for Hibernate. It simply
 * stores the milliseconds as a bigint.
 * 
 * @author Daniel Jurado (jurado@gmail.com)
 */
public class PersistentDurationAsMilliseconds implements UserType, Serializable {

	private static final int[] SQL_TYPES = new int[] { Types.BIGINT };

	public Class returnedClass() {
		return Duration.class;
	}

	public int[] sqlTypes() {
		return SQL_TYPES;
	}

	public Object nullSafeGet(ResultSet resultSet, String[] strings,
			Object object) throws HibernateException, SQLException {
		BigInteger b = (BigInteger) StandardBasicTypes.BIG_INTEGER.nullSafeGet(
				resultSet, strings[0]);
		if (b == null) {
			return null;
		}

		return new Duration(b.longValue());
	}

	public void nullSafeSet(PreparedStatement preparedStatement, Object value,
			int index) throws HibernateException, SQLException {
		if (value == null) {
			StandardBasicTypes.BIG_INTEGER.nullSafeSet(preparedStatement, null,
					index);
		} else {
			StandardBasicTypes.BIG_INTEGER.nullSafeSet(preparedStatement,
					BigInteger.valueOf((Long) value), index);
		}
	}

	public boolean equals(Object x, Object y) throws HibernateException {
		if (x == y) {
			return true;
		}
		if (x == null || y == null) {
			return false;
		}
		return x.equals(y);
	}

	public int hashCode(Object object) throws HibernateException {
		return object.hashCode();
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

	public Object assemble(Serializable cached, Object value)
			throws HibernateException {
		return cached;
	}

	public Object replace(Object original, Object target, Object owner)
			throws HibernateException {
		return original;
	}

}
