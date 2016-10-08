package org.joda.time.contrib.hibernate;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.usertype.EnhancedUserType;
import org.joda.time.Period;

/**
 * Converts a org.joda.time.Period to and from Sql for Hibernate. It simply
 * stores and retrieves the value as an integer using its milliseconds.
 * 
 * @author Daniel Jurado (jurado@gmail.com)
 */
public class PersistentPeriodAsMilliseconds implements EnhancedUserType,
		Serializable {
	public static final PersistentPeriodAsMilliseconds INSTANCE = new PersistentPeriodAsMilliseconds();

	private static final int[] SQL_TYPES = new int[] { Types.INTEGER, };

	public Object assemble(Serializable cached, Object value)
			throws HibernateException {
		return cached;
	}

	public Object deepCopy(Object value) throws HibernateException {
		return value;
	}

	public Serializable disassemble(Object value) throws HibernateException {
		return (Serializable) value;
	}

	public boolean equals(Object x, Object y) throws HibernateException {
		if (x == y) {
			return true;
		}
		if (x == null || y == null) {
			return false;
		}
		Period dtx = (Period) x;
		Period dty = (Period) y;
		return dtx.equals(dty);
	}

	public Object fromXMLString(String string) {
		return new Period(string);
	}

	public int hashCode(Object object) throws HibernateException {
		return object.hashCode();
	}

	public boolean isMutable() {
		return false;
	}

	public Object nullSafeGet(ResultSet resultSet, String string)
			throws SQLException {
		Object milliseconds = StandardBasicTypes.INTEGER.nullSafeGet(resultSet,
				string);
		if (milliseconds == null) {
			return null;
		}

		return Period.millis((Integer) milliseconds);
	}

	public Object nullSafeGet(ResultSet resultSet, String[] strings,
			Object object) throws HibernateException, SQLException {
		return nullSafeGet(resultSet, strings[0]);

	}

	public void nullSafeSet(PreparedStatement preparedStatement, Object value,
			int index) throws HibernateException, SQLException {
		if (value == null) {
			StandardBasicTypes.INTEGER.nullSafeSet(preparedStatement, null,
					index);
		} else {
			Period p = (Period) value;
			StandardBasicTypes.INTEGER.nullSafeSet(preparedStatement,
					p.getMillis(), index);
		}
	}

	public String objectToSQLString(Object object) {
		throw new UnsupportedOperationException();
	}

	public Object replace(Object original, Object target, Object owner)
			throws HibernateException {
		return original;
	}

	public Class<?> returnedClass() {
		return Period.class;
	}

	public int[] sqlTypes() {
		return SQL_TYPES;
	}

	public String toXMLString(Object object) {
		return object.toString();
	}
}
