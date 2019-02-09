package com.kiyosuke.sqlitlin.db

import com.kiyosuke.sqlitlin.db.table.Table
import org.junit.Assert.*
import org.junit.Ignore
import org.junit.Test

class QueryTest {

    object TestUser : Table("users") {
        val id = integer("id").primaryKey()
        val name = text("name")
        val age = integer("age").nullable()
        val birthday = text("birthday").nullable()
    }

    @Ignore
    private fun select(query: Select<TestUser>.() -> Unit): String {
        return Select(TestUser).apply(query).toSql()
    }

    @Test
    fun selectAge20UserSql() {
        val expected = "SELECT * FROM users WHERE age = 20"
        val actual = select {
            where {
                it.age.eq(20)
            }
        }
        assertEquals(expected, actual)
    }

    @Test
    fun selectAge30OrderByAgeAsc() {
        val expected = "SELECT * FROM users WHERE age = 30 ORDER BY age ASC"
        val actual = select {
            where {
                it.age.eq(30)
            }
            orderBy(TestUser.age to OrderBy.SortOrder.ASC)
        }
        assertEquals(expected, actual)
    }

    @Test
    fun selectNameLikeK() {
        val expected = "SELECT * FROM users WHERE name LIKE '%k%'"
        val actual = select {
            where {
                it.name.like("%k%")
            }
        }
        assertEquals(expected, actual)
    }

    @Test
    fun selectAge20or30Limit30() {
        val expected = "SELECT * FROM users WHERE age = 20 OR age = 30 LIMIT 30 OFFSET 0"
        val actual = select {
            where {
                it.age.eq(20) or it.age.eq(30)
            }
            limit(30)
        }
        assertEquals(expected, actual)
    }

    @Test
    fun selectAge20_30() {
        val expected = "SELECT * FROM users WHERE age >= 20 AND age <= 30"
        val actual = select {
            where {
                it.age.greaterEq(20) and it.age.lessEq(30)
            }
        }
        assertEquals(expected, actual)
    }

    @Test
    fun selectBetween10_30() {
        val expected = "SELECT * FROM users WHERE age BETWEEN 10 AND 20"
        val actual = select {
            where {
                it.age.between(10 to 20)
            }
        }
        assertEquals(expected, actual)
    }

    @Test
    fun selectAge20_30OrNameLikeK() {
        val expected = "SELECT * FROM users WHERE age >= 20 AND age <= 30 OR name LIKE '%k%'"
        val actual = select {
            where {
                it.age.greaterEq(20) and it.age.lessEq(30) or it.name.like("%k%")
            }
        }
        assertEquals(expected, actual)
    }

    @Test
    fun selectAgeInList10_20_30() {
        val expected = "SELECT * FROM users WHERE age IN(10, 20, 30)"
        val actual = select {
            where {
                it.age.inList(listOf(10, 20, 30))
            }
        }
        assertEquals(expected, actual)
    }

    @Test
    fun selectAgeNotInList10_20_30() {
        val expected = "SELECT * FROM users WHERE age NOT IN(10, 20, 30)"
        val actual = select {
            where {
                it.age.notInList(listOf(10, 20, 30))
            }
        }
        assertEquals(expected, actual)
    }


}