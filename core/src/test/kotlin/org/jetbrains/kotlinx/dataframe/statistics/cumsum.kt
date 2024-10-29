package org.jetbrains.kotlinx.dataframe.statistics

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.cumSum
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.map
import org.junit.Test

@Suppress("ktlint:standard:argument-list-wrapping")
class CumsumTests {

    val col by columnOf(1, 2, null, 3, 4)
    val expected = listOf(1, 3, null, 6, 10)
    val expectedNoSkip = listOf(1, 3, null, null, null)

    @Test
    fun `int column`() {
        col.cumSum().toList() shouldBe expected
        col.cumSum(skipNA = false).toList() shouldBe expectedNoSkip
    }

    @Test
    fun `short column`() {
        col.map { it?.toShort() }.cumSum().toList() shouldBe expected.map { it?.toShort() }
        col.map { it?.toShort() }.cumSum(skipNA = false).toList() shouldBe expectedNoSkip.map { it?.toShort() }
    }

    @Test
    fun `byte column`() {
        col.map { it?.toByte() }.cumSum().toList() shouldBe expected.map { it?.toByte() }
        col.map { it?.toByte() }.cumSum(skipNA = false).toList() shouldBe expectedNoSkip.map { it?.toByte() }
    }

    @Test
    fun `big int column`() {
        col.map { it?.toBigInteger() }.cumSum().toList() shouldBe expected.map { it?.toBigInteger() }
        col.map { it?.toBigInteger() }.cumSum(skipNA = false).toList() shouldBe expectedNoSkip.map { it?.toBigInteger() }
    }

    @Test
    fun `big decimal column`() {
        col.map { it?.toBigDecimal() }.cumSum().toList() shouldBe expected.map { it?.toBigDecimal() }
        col.map { it?.toBigDecimal() }.cumSum(skipNA = false).toList() shouldBe expectedNoSkip.map { it?.toBigDecimal() }
    }

    @Test
    fun frame() {
        val str by columnOf("a", "b", "c", "d", "e")
        val df = dataFrameOf(col, str)

        df.cumSum()[col].toList() shouldBe expected
        df.cumSum(skipNA = false)[col].toList() shouldBe expectedNoSkip

        df.cumSum { col }[col].toList() shouldBe expected
        df.cumSum(skipNA = false) { col }[col].toList() shouldBe expectedNoSkip

        df.cumSum(col)[col].toList() shouldBe expected
        df.cumSum(col, skipNA = false)[col].toList() shouldBe expectedNoSkip
    }

    @Test
    fun `double column`() {
        val doubles by columnOf(1.0, 2.0, null, Double.NaN, 4.0)
        doubles.cumSum().toList() shouldBe listOf(1.0, 3.0, Double.NaN, Double.NaN, 7.0)
    }

    @Test
    fun `number column`() {
        val doubles: DataColumn<Number?> by columnOf(1, 2, null, Double.NaN, 4)
        doubles.cumSum().toList() shouldBe listOf(1.0, 3.0, Double.NaN, Double.NaN, 7.0)
    }

    @Test
    fun `groupBy`() {
        val df = dataFrameOf("str", "col")(
            "a", 1,
            "b", 2,
            "c", null,
            "a", 3,
            "c", 4,
        )
        df.groupBy("str").cumSum().concat() shouldBe
            dataFrameOf("str", "col")(
                "a", 1,
                "a", 4,
                "b", 2,
                "c", null,
                "c", 4,
            )
    }
}
