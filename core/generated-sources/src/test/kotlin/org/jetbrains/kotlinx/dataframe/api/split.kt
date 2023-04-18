package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.hasNulls
import org.jetbrains.kotlinx.dataframe.impl.DataRowImpl
import org.jetbrains.kotlinx.dataframe.type
import org.junit.Test
import kotlin.reflect.typeOf

class SplitTests {

    val stringPairDf = dataFrameOf("first", "second")("22-65", "22-66")
    val listPairDf = dataFrameOf("first", "second")(listOf("22", "65"), listOf("22", "66"))

    @Test
    fun `split with default`() {
        val recentDelays = listOf(listOf(23, 47), listOf(), listOf(24, 43, 87), listOf(13), listOf(67, 32)).toColumn("RecentDelays")
        val df = dataFrameOf(recentDelays)
        val split = df.split(recentDelays).default(0).into { "delay$it" }
        split.columns().forEach {
            it.hasNulls() shouldBe false
        }
        split.values().count { it == 0 } shouldBe 7
    }

    @Test
    fun `split with regex`() {
        val title by columnOf(
            "Toy Story (1995)",
            "Jumanji (1995)",
            "Grumpier Old Men (1995)",
            "Waiting to Exhale (1995)"
        )

        val regex = """(.*) \((\d{4})\)""".toRegex()
        val split = title.toDataFrame()
            .split { title }
            .match(regex)
            .into("title", "year")
            .parse()
        split.schema().print()
        split["title"].hasNulls shouldBe false
        split["year"].type shouldBe typeOf<Int>()
    }

    @Test
    fun `split into columns`() {
        val df = dataFrameOf("a", "b", "c")(
            1, 2, 3,
            1, 4, 5,
            2, 3, 4,
            3, 6, 7
        )
        val res = df.groupBy("a").updateGroups { it.remove("a") }.into("g")
            .update("g").at(1).with { DataFrame.empty() }
            .update("g").at(2).withNull()
            .split { "g"<AnyFrame>() }.intoColumns()
            .ungroup("g")
        res shouldBe dataFrameOf("a", "b", "c")(
            1, listOf(2, 4), listOf(3, 5),
            2, emptyList<Int>(), emptyList<Int>(),
            3, emptyList<Int>(), emptyList<Int>()
        )
    }

    @Test
    fun `split string by delimiter inward`() {
        val res = stringPairDf.split("first", "second").by("-").inward("left", "right")

        res shouldBe dataFrameOf(
            columnOf(columnOf("22") named "left", columnOf("65") named "right") named "first",
            columnOf(columnOf("22") named "left", columnOf("66") named "right") named "second"
        )
    }

    @Test
    fun `split string by delimiter into columns with suffixes`() {
        val res = stringPairDf.split("first", "second").by("-").into("left", "right")

        res shouldBe dataFrameOf(
            columnOf("22") named "left",
            columnOf("65") named "right",
            columnOf("22") named "left1",
            columnOf("66") named "right1"
        )
    }

    @Test
    fun `split list inward with autogenerated names`() {
        val res = listPairDf.split { "first"<List<String>>() and "second"<List<String>>() }.inward()

        res shouldBe dataFrameOf(
            columnOf(columnOf("22") named "split1", columnOf("65") named "split2") named "first",
            columnOf(columnOf("22") named "split1", columnOf("66") named "split2") named "second"
        )
    }

    @Test
    fun `split list into with autogenerated names`() {
        val res = listPairDf.split { "first"<List<String>>() and "second"<List<String>>() }.into()

        res shouldBe dataFrameOf(
            columnOf("22") named "split1",
            columnOf("65") named "split2",
            columnOf("22") named "split3",
            columnOf("66") named "split4"
        )
    }

    @Test
    fun `sequence of splits with autogenerated names`() {
        var res = listPairDf.split { "first"<List<String>>() }.into()
        res = res.split { "second"<List<String>>() }.into()

        res shouldBe dataFrameOf(
            columnOf("22") named "split1",
            columnOf("65") named "split2",
            columnOf("22") named "split3",
            columnOf("66") named "split4"
        )
    }

    @Test
    fun `split column group inward`() {
        val df = stringPairDf.group("first", "second").into("group")

        // Note: this operation replaces original columns in group so there is no name conflict
        val res = df.split { "group"<DataRowImpl<*>>() }
            .by { it -> listOf(it[1], it[0]) } // swap columns
            .inward("first", "second") // no name conflict

        res shouldBe dataFrameOf(
            columnOf(columnOf("22-66") named "first", columnOf("22-65") named "second") named "group"
        )
    }

    @Test
    fun `split column group into hierarchy with correct names`() {
        val df = dataFrameOf(
            columnOf(
                columnOf("a") named "first",
                columnOf(
                    columnOf("b") named "first",
                    columnOf("c") named "second"
                ) named "nestedGroup"
            ) named "topLevelGroup",
            columnOf("d") named "first",
        )

        val topLevelGroup by columnGroup()
        val nestedGroup by topLevelGroup.columnGroup()

        val res = df.split { nestedGroup }
            .by { it -> listOf(it[0], it[1]) }
            .into("first", "second") // name conflict

        res shouldBe dataFrameOf(
            columnOf(
                columnOf("a") named "first",
                columnOf("b") named "first1",
                columnOf("c") named "second"
            ) named "topLevelGroup",
            columnOf("d") named "first",
        )
    }
}