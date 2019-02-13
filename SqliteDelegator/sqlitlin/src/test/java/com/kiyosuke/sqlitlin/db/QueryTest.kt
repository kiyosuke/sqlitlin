package com.kiyosuke.sqlitlin.db

import com.kiyosuke.sqlitlin.db.column.Column
import com.kiyosuke.sqlitlin.db.table.Table
import org.junit.Assert.assertEquals
import org.junit.Ignore
import org.junit.Test

class QueryTest {

    object TestUser : Table("users") {
        val id = integer("id").primaryKey()
        val name = text("name")
        val age = integer("age").nullable()
        val birthday = text("birthday").nullable()
    }

    object TestJobs : Table("jobs") {
        val id = integer("id").primaryKey()
        val userId = integer("user_id")
        val name = text("name")
        val type = integer("type")
    }

    object TestFamily : Table("families") {
        val id = integer("id")
        val userId = integer("user_id")
        val number = integer("number")
    }

    @Ignore
    private fun select(query: Select<TestUser>.() -> Unit): String {
        return Select(TestUser.columns, TestUser).apply(query).toSql()
    }

    fun <JT : Table> innerJoin(joinTable: JT, onColumn: Column<*>, joinColumn: Column<*>): InnerJoin {
        return InnerJoin(joinTable, onColumn, joinColumn)
    }

    fun Join.select(
        vararg columns: Column<*> = emptyArray(),
        query: Select<TestUser>.() -> Unit
    ): String {
        if (columns.isEmpty()) throw IllegalArgumentException("columns is empty.")
        return Select(listOf(*columns), TestUser, this@select).apply(query).toSql()
    }

    @Test
    fun selectAge20UserSql() {
        val expected =
            "SELECT users.id AS users_id,users.name AS users_name,users.age AS users_age,users.birthday AS users_birthday FROM users WHERE users.age = 20"
        val actual = select {
            where {
                it.age.eq(20)
            }
        }
        assertEquals(expected, actual)
    }

    @Test
    fun selectAge30OrderByAgeAsc() {
        val expected =
            "SELECT users.id AS users_id,users.name AS users_name,users.age AS users_age,users.birthday AS users_birthday FROM users WHERE users.age = 30 ORDER BY users.age ASC"
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
        val expected =
            "SELECT users.id AS users_id,users.name AS users_name,users.age AS users_age,users.birthday AS users_birthday FROM users WHERE users.name LIKE '%k%'"
        val actual = select {
            where {
                it.name.like("%k%")
            }
        }
        assertEquals(expected, actual)
    }

    @Test
    fun selectAge20or30Limit30() {
        val expected =
            "SELECT users.id AS users_id,users.name AS users_name,users.age AS users_age,users.birthday AS users_birthday FROM users WHERE users.age = 20 OR users.age = 30 LIMIT 30 OFFSET 0"
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
        val expected =
            "SELECT users.id AS users_id,users.name AS users_name,users.age AS users_age,users.birthday AS users_birthday FROM users WHERE users.age >= 20 AND users.age <= 30"
        val actual = select {
            where {
                it.age.greaterEq(20) and it.age.lessEq(30)
            }
        }
        assertEquals(expected, actual)
    }

    @Test
    fun selectBetween10_30() {
        val expected =
            "SELECT users.id AS users_id,users.name AS users_name,users.age AS users_age,users.birthday AS users_birthday FROM users WHERE users.age BETWEEN 10 AND 20"
        val actual = select {
            where {
                it.age.between(10 to 20)
            }
        }
        assertEquals(expected, actual)
    }

    @Test
    fun selectAge20_30OrNameLikeK() {
        val expected =
            "SELECT users.id AS users_id,users.name AS users_name,users.age AS users_age,users.birthday AS users_birthday FROM users WHERE users.age >= 20 AND users.age <= 30 OR users.name LIKE '%k%'"
        val actual = select {
            where {
                it.age.greaterEq(20) and it.age.lessEq(30) or it.name.like("%k%")
            }
        }
        assertEquals(expected, actual)
    }

    @Test
    fun innerJoinOnIdSelectAll() {
        val expected =
            "SELECT users.id AS users_id,users.name AS users_name,users.age AS users_age,jobs.name AS jobs_name FROM users INNER JOIN jobs ON users.id = jobs.user_id"
        val actual = innerJoin(TestJobs, onColumn = TestUser.id, joinColumn = TestJobs.userId)
            .select(TestUser.id, TestUser.name, TestUser.age, TestJobs.name) {}
        assertEquals(expected, actual)
    }

    @Test
    fun selectAgeInList10_20_30() {
        val expected = "SELECT users.id AS users_id,users.name AS users_name,users.age AS users_age,users.birthday AS users_birthday FROM users WHERE users.age IN(10, 20, 30)"
        val actual = select {
            where {
                it.age.inList(listOf(10, 20, 30))
            }
        }
        assertEquals(expected, actual)
    }

    @Test
    fun userInnerJoinJobsOnIdAndInnerJoinFamiliesOnId() {
        val expected =
            "SELECT users.id AS users_id,users.name AS users_name,users.age AS users_age,jobs.name AS jobs_name,families.number AS families_number FROM users INNER JOIN jobs ON users.id = jobs.user_id INNER JOIN families ON users.id = families.user_id"
        val actual = innerJoin(TestJobs, onColumn = TestUser.id, joinColumn = TestJobs.userId)
            .innerJoin(TestFamily, onColumn = TestUser.id, joinColumn = TestFamily.userId)
            .select(TestUser.id, TestUser.name, TestUser.age, TestJobs.name, TestFamily.number) { }
        assertEquals(expected, actual)
    }

    @Test
    fun selectAgeNotInList10_20_30() {
        val expected = "SELECT users.id AS users_id,users.name AS users_name,users.age AS users_age,users.birthday AS users_birthday FROM users WHERE users.age NOT IN(10, 20, 30)"
        val actual = select {
            where {
                it.age.notInList(listOf(10, 20, 30))
            }
        }
        assertEquals(expected, actual)
    }

}