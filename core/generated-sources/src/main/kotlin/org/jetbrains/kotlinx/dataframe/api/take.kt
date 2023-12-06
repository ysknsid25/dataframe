package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnFilter
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowFilter
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.size
import org.jetbrains.kotlinx.dataframe.documentation.CommonTakeAndDropDocs
import org.jetbrains.kotlinx.dataframe.documentation.CommonTakeAndDropWhileDocs
import org.jetbrains.kotlinx.dataframe.documentation.TakeAndDropColumnsSelectionDslUsage
import org.jetbrains.kotlinx.dataframe.impl.columns.transform
import org.jetbrains.kotlinx.dataframe.impl.columns.transformSingle
import org.jetbrains.kotlinx.dataframe.index
import org.jetbrains.kotlinx.dataframe.nrow
import kotlin.reflect.KProperty

// region DataColumn

public fun <T> DataColumn<T>.take(n: Int): DataColumn<T> = when {
    n == 0 -> get(emptyList())
    n >= size -> this
    else -> get(0 until n)
}

public fun <T> DataColumn<T>.takeLast(n: Int = 1): DataColumn<T> = drop(size - n)

// endregion

// region DataFrame

/**
 * Returns a DataFrame containing first [n] rows.
 *
 * @throws IllegalArgumentException if [n] is negative.
 */
public fun <T> DataFrame<T>.take(n: Int): DataFrame<T> {
    require(n >= 0) { "Requested rows count $n is less than zero." }
    return getRows(0 until n.coerceAtMost(nrow))
}

/**
 * Returns a DataFrame containing last [n] rows.
 *
 * @throws IllegalArgumentException if [n] is negative.
 */
public fun <T> DataFrame<T>.takeLast(n: Int = 1): DataFrame<T> {
    require(n >= 0) { "Requested rows count $n is less than zero." }
    return drop((nrow - n).coerceAtLeast(0))
}

/**
 * Returns a DataFrame containing first rows that satisfy the given [predicate].
 */
public fun <T> DataFrame<T>.takeWhile(predicate: RowFilter<T>): DataFrame<T> =
    firstOrNull { !predicate(it, it) }?.let { take(it.index) } ?: this

// endregion

// region ColumnsSelectionDsl

public interface TakeColumnsSelectionDsl {

    /**
     * ## Take (Last) (Cols) (While) Usage
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `columnSet: `[ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]`<*>`
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `columnGroup: `[SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]`<`[DataRow][org.jetbrains.kotlinx.dataframe.DataRow]`<*>> | `[String][String]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     * `| `[KProperty][kotlin.reflect.KProperty]`<*>` | `[ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `condition: `[ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter]
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `number: `[Int][Int]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]:
     *
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [**take**][ColumnsSelectionDsl.take]`(`[**Last**][ColumnsSelectionDsl.takeLast]`)`**`(`**[number][org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.NumberDef]**`)`**
     *
     *  `|` [**take**][ColumnsSelectionDsl.takeWhile]`(`[**Last**][ColumnsSelectionDsl.takeLastWhile]`)`[**While**][ColumnsSelectionDsl.takeWhile]**` { `**[condition][org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.ConditionDef]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### On a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     *
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [columnSet][org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.ColumnSetDef]
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;.[**take**][ColumnsSelectionDsl.take]`(`[**Last**][ColumnSet.takeLast]`)`**`(`**[number][org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.NumberDef]**`)`**
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;`|` .[**take**][ColumnsSelectionDsl.takeWhile]`(`[**Last**][ColumnsSelectionDsl.takeLastWhile]`)`[**While**][ColumnsSelectionDsl.takeWhile]**` { `**[condition][org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.ConditionDef]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### On a column group reference:
     *
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [columnGroup][org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.ColumnGroupDef]
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;.[**take**][ColumnsSelectionDsl.takeCols]`(`[**Last**][ColumnsSelectionDsl.takeLastCols]`)`[**Cols**][ColumnsSelectionDsl.takeCols]**`(`**[number][org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.NumberDef]**`)`**
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;`|` .[**take**][ColumnsSelectionDsl.takeColsWhile]`(`[**Last**][ColumnsSelectionDsl.takeLastColsWhile]`)`[**ColsWhile**][ColumnsSelectionDsl.takeColsWhile]**` { `**[condition][org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.ConditionDef]**` }`**
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     */
    public interface Usage {

        /** [**take**][ColumnsSelectionDsl.take]`(`[**Last**][ColumnsSelectionDsl.takeLast]`)` */
        public interface PlainDslName

        /** .[**take**][ColumnsSelectionDsl.take]`(`[**Last**][ColumnSet.takeLast]`)` */
        public interface ColumnSetName

        /** .[**take**][ColumnsSelectionDsl.takeCols]`(`[**Last**][ColumnsSelectionDsl.takeLastCols]`)`[**Cols**][ColumnsSelectionDsl.takeCols] */
        public interface ColumnGroupName

        /** [**take**][ColumnsSelectionDsl.takeWhile]`(`[**Last**][ColumnsSelectionDsl.takeLastWhile]`)`[**While**][ColumnsSelectionDsl.takeWhile] */
        public interface PlainDslWhileName

        /** .[**take**][ColumnsSelectionDsl.takeWhile]`(`[**Last**][ColumnsSelectionDsl.takeLastWhile]`)`[**While**][ColumnsSelectionDsl.takeWhile] */
        public interface ColumnSetWhileName

        /** .[**take**][ColumnsSelectionDsl.takeColsWhile]`(`[**Last**][ColumnsSelectionDsl.takeLastColsWhile]`)`[**ColsWhile**][ColumnsSelectionDsl.takeColsWhile] */
        public interface ColumnGroupWhileName
    }

    // region take

    /**
     * ## Take (Cols)
     * This function takes the first [n] columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [takeCols][SingleColumn.takeCols] will take the first [n] columns of that column group.
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [take][ColumnSet.take] will take the first [n] columns of that column set.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `take` is called `takeCols` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * See [Usage] for how to use these functions.
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[take][ColumnSet.take]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[take][ColumnsSelectionDsl.take]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeCols][SingleColumn.takeCols]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeCols][String.takeCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [CommonTakeAndDropDocs.ExampleArg][org.jetbrains.kotlinx.dataframe.documentation.CommonTakeAndDropDocs.ExampleArg]}
     *
     * @param [n] The number of columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first [n] columns.
     */
    private interface CommonTakeFirstDocs

    /**
     * ## Take (Cols)
     * This function takes the first [n] columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [takeCols][SingleColumn.takeCols] will take the first [n] columns of that column group.
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [take][ColumnSet.take] will take the first [n] columns of that column set.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `take` is called `takeCols` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * See [Usage] for how to use these functions.
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[take][ColumnSet.take]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[take][ColumnsSelectionDsl.take]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeCols][SingleColumn.takeCols]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeCols][String.takeCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[take][ColumnSet.take]`(2) }`
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[take][ColumnSet.take]`(2) }`
     *
     * @param [n] The number of columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first [n] columns.
     */
    public fun <C> ColumnSet<C>.take(n: Int): ColumnSet<C> = transform { it.take(n) }

    /**
     * ## Take (Cols)
     * This function takes the first [n] columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [takeCols][SingleColumn.takeCols] will take the first [n] columns of that column group.
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [take][ColumnSet.take] will take the first [n] columns of that column set.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `take` is called `takeCols` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * See [Usage] for how to use these functions.
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[take][ColumnSet.take]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[take][ColumnsSelectionDsl.take]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeCols][SingleColumn.takeCols]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeCols][String.takeCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[take][ColumnsSelectionDsl.take]`(5) }`
     *
     * @param [n] The number of columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first [n] columns.
     */
    public fun ColumnsSelectionDsl<*>.take(n: Int): ColumnSet<*> =
        this.asSingleColumn().takeCols(n)

    /**
     * ## Take (Cols)
     * This function takes the first [n] columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [takeCols][SingleColumn.takeCols] will take the first [n] columns of that column group.
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [take][ColumnSet.take] will take the first [n] columns of that column set.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `take` is called `takeCols` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * See [Usage] for how to use these functions.
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[take][ColumnSet.take]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[take][ColumnsSelectionDsl.take]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeCols][SingleColumn.takeCols]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeCols][String.takeCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[takeCols][SingleColumn.takeCols]`(1) }`
     *
     * @param [n] The number of columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first [n] columns.
     */
    public fun SingleColumn<DataRow<*>>.takeCols(n: Int): ColumnSet<*> =
        this.ensureIsColumnGroup().transformSingle { it.cols().take(n) }

    /**
     * ## Take (Cols)
     * This function takes the first [n] columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [takeCols][SingleColumn.takeCols] will take the first [n] columns of that column group.
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [take][ColumnSet.take] will take the first [n] columns of that column set.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `take` is called `takeCols` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * See [Usage] for how to use these functions.
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[take][ColumnSet.take]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[take][ColumnsSelectionDsl.take]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeCols][SingleColumn.takeCols]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeCols][String.takeCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[takeCols][String.takeCols]`(1) }`
     *
     * @param [n] The number of columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first [n] columns.
     */
    public fun String.takeCols(n: Int): ColumnSet<*> = columnGroup(this).takeCols(n)

    /**
     * ## Take (Cols)
     * This function takes the first [n] columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [takeCols][SingleColumn.takeCols] will take the first [n] columns of that column group.
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [take][ColumnSet.take] will take the first [n] columns of that column set.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `take` is called `takeCols` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * See [Usage] for how to use these functions.
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[take][ColumnSet.take]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[take][ColumnsSelectionDsl.take]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeCols][SingleColumn.takeCols]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeCols][String.takeCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[takeCols][SingleColumn.takeCols]`(1) }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[takeCols][KProperty.takeCols]`(1) }`
     *
     * @param [n] The number of columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first [n] columns.
     */
    public fun KProperty<*>.takeCols(n: Int): ColumnSet<*> = columnGroup(this).takeCols(n)

    /**
     * ## Take (Cols)
     * This function takes the first [n] columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [takeCols][SingleColumn.takeCols] will take the first [n] columns of that column group.
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [take][ColumnSet.take] will take the first [n] columns of that column set.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `take` is called `takeCols` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * See [Usage] for how to use these functions.
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[take][ColumnSet.take]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[take][ColumnsSelectionDsl.take]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeCols][SingleColumn.takeCols]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeCols][String.takeCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[takeCols][ColumnPath.takeCols]`(1) }`
     *
     * @param [n] The number of columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first [n] columns.
     */
    public fun ColumnPath.takeCols(n: Int): ColumnSet<*> = columnGroup(this).takeCols(n)

    // endregion

    // region takeLast

    /**
     * ## Take Last (Cols)
     * This function takes the last [n] columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [takeLastCols][SingleColumn.takeLastCols] will take the last [n] columns of that column group.
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [takeLast][ColumnSet.takeLast] will take the last [n] columns of that column set.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeLast` is called `takeLastCols` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * See [Usage] for how to use these functions.
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[takeLast][ColumnSet.takeLast]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[takeLast][ColumnsSelectionDsl.takeLast]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeLastCols][SingleColumn.takeLastCols]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeLastCols][String.takeLastCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [CommonTakeAndDropDocs.ExampleArg][org.jetbrains.kotlinx.dataframe.documentation.CommonTakeAndDropDocs.ExampleArg]}
     *
     * @param [n] The number of columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last [n] columns.
     */
    private interface CommonTakeLastDocs

    /**
     * ## Take Last (Cols)
     * This function takes the last [n] columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [takeLastCols][SingleColumn.takeLastCols] will take the last [n] columns of that column group.
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [takeLast][ColumnSet.takeLast] will take the last [n] columns of that column set.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeLast` is called `takeLastCols` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * See [Usage] for how to use these functions.
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[takeLast][ColumnSet.takeLast]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[takeLast][ColumnsSelectionDsl.takeLast]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeLastCols][SingleColumn.takeLastCols]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeLastCols][String.takeLastCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[takeLast][ColumnSet.takeLast]`(2) }`
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[takeLast][ColumnSet.takeLast]`(2) }`
     *
     * @param [n] The number of columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last [n] columns.
     */
    public fun <C> ColumnSet<C>.takeLast(n: Int = 1): ColumnSet<C> = transform { it.takeLast(n) }

    /**
     * ## Take Last (Cols)
     * This function takes the last [n] columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [takeLastCols][SingleColumn.takeLastCols] will take the last [n] columns of that column group.
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [takeLast][ColumnSet.takeLast] will take the last [n] columns of that column set.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeLast` is called `takeLastCols` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * See [Usage] for how to use these functions.
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[takeLast][ColumnSet.takeLast]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[takeLast][ColumnsSelectionDsl.takeLast]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeLastCols][SingleColumn.takeLastCols]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeLastCols][String.takeLastCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[takeLast][ColumnsSelectionDsl.takeLast]`(5) }`
     *
     * @param [n] The number of columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last [n] columns.
     */
    public fun ColumnsSelectionDsl<*>.takeLast(n: Int = 1): ColumnSet<*> =
        asSingleColumn().takeLastCols(n)

    /**
     * ## Take Last (Cols)
     * This function takes the last [n] columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [takeLastCols][SingleColumn.takeLastCols] will take the last [n] columns of that column group.
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [takeLast][ColumnSet.takeLast] will take the last [n] columns of that column set.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeLast` is called `takeLastCols` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * See [Usage] for how to use these functions.
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[takeLast][ColumnSet.takeLast]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[takeLast][ColumnsSelectionDsl.takeLast]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeLastCols][SingleColumn.takeLastCols]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeLastCols][String.takeLastCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[takeLast][SingleColumn.takeLastCols]`(1) }`
     *
     * @param [n] The number of columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last [n] columns.
     */
    public fun SingleColumn<DataRow<*>>.takeLastCols(n: Int): ColumnSet<*> =
        this.ensureIsColumnGroup().transformSingle { it.cols().takeLast(n) }

    /**
     * ## Take Last (Cols)
     * This function takes the last [n] columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [takeLastCols][SingleColumn.takeLastCols] will take the last [n] columns of that column group.
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [takeLast][ColumnSet.takeLast] will take the last [n] columns of that column set.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeLast` is called `takeLastCols` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * See [Usage] for how to use these functions.
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[takeLast][ColumnSet.takeLast]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[takeLast][ColumnsSelectionDsl.takeLast]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeLastCols][SingleColumn.takeLastCols]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeLastCols][String.takeLastCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[takeLastCols][String.takeLastCols]`(1) }`
     *
     * @param [n] The number of columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last [n] columns.
     */
    public fun String.takeLastCols(n: Int): ColumnSet<*> = columnGroup(this).takeLastCols(n)

    /**
     * ## Take Last (Cols)
     * This function takes the last [n] columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [takeLastCols][SingleColumn.takeLastCols] will take the last [n] columns of that column group.
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [takeLast][ColumnSet.takeLast] will take the last [n] columns of that column set.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeLast` is called `takeLastCols` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * See [Usage] for how to use these functions.
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[takeLast][ColumnSet.takeLast]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[takeLast][ColumnsSelectionDsl.takeLast]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeLastCols][SingleColumn.takeLastCols]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeLastCols][String.takeLastCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[takeLastCols][SingleColumn.takeLastCols]`(1) }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[takeLastCols][KProperty.takeLastCols]`(1) }`
     *
     * @param [n] The number of columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last [n] columns.
     */
    public fun KProperty<*>.takeLastCols(n: Int): ColumnSet<*> = columnGroup(this).takeLastCols(n)

    /**
     * ## Take Last (Cols)
     * This function takes the last [n] columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [takeLastCols][SingleColumn.takeLastCols] will take the last [n] columns of that column group.
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [takeLast][ColumnSet.takeLast] will take the last [n] columns of that column set.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeLast` is called `takeLastCols` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * See [Usage] for how to use these functions.
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[takeLast][ColumnSet.takeLast]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[takeLast][ColumnsSelectionDsl.takeLast]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeLastCols][SingleColumn.takeLastCols]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeLastCols][String.takeLastCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[takeLastCols][ColumnPath.takeLastCols]`(1) }`
     *
     * @param [n] The number of columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last [n] columns.
     */
    public fun ColumnPath.takeLastCols(n: Int): ColumnSet<*> = columnGroup(this).takeLastCols(n)

    // endregion

    // region takeWhile

    /**
     * ## Take (Cols) While
     * This function takes the first columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or
     * [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] adhering to the given [predicate].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [takeWhile][SingleColumn.takeColsWhile] will take the
     * first cols of that column group adhering to the given [predicate].
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [takeWhile][ColumnSet.takeWhile] will
     * take the first columns of that column set adhering to the given [predicate].
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeWhile` is called
     * `takeColsWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * See [Usage] for how to use these functions.
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[takeWhile][ColumnSet.takeWhile]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeWhile][SingleColumn.takeColsWhile]` { it.`[any][org.jetbrains.kotlinx.dataframe.DataColumn.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeColsWhile][String.takeColsWhile]` { it.`[kind][org.jetbrains.kotlinx.dataframe.DataColumn.kind]`() == `[ColumnKind.Value][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [CommonTakeAndDropWhileDocs.ExampleArg][org.jetbrains.kotlinx.dataframe.documentation.CommonTakeAndDropWhileDocs.ExampleArg]}
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first columns adhering to the [predicate].
     */
    private interface CommonTakeFirstWhileDocs

    /**
     * ## Take (Cols) While
     * This function takes the first columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or
     * [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] adhering to the given [predicate].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [takeWhile][SingleColumn.takeColsWhile] will take the
     * first cols of that column group adhering to the given [predicate].
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [takeWhile][ColumnSet.takeWhile] will
     * take the first columns of that column set adhering to the given [predicate].
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeWhile` is called
     * `takeColsWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * See [Usage] for how to use these functions.
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[takeWhile][ColumnSet.takeWhile]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeWhile][SingleColumn.takeColsWhile]` { it.`[any][org.jetbrains.kotlinx.dataframe.DataColumn.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeColsWhile][String.takeColsWhile]` { it.`[kind][org.jetbrains.kotlinx.dataframe.DataColumn.kind]`() == `[ColumnKind.Value][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[takeWhile][ColumnSet.takeWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[takeWhile][ColumnSet.takeWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first columns adhering to the [predicate].
     */
    public fun <C> ColumnSet<C>.takeWhile(predicate: ColumnFilter<C>): ColumnSet<C> =
        transform { it.takeWhile(predicate) }

    /**
     * ## Take (Cols) While
     * This function takes the first columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or
     * [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] adhering to the given [predicate].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [takeWhile][SingleColumn.takeColsWhile] will take the
     * first cols of that column group adhering to the given [predicate].
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [takeWhile][ColumnSet.takeWhile] will
     * take the first columns of that column set adhering to the given [predicate].
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeWhile` is called
     * `takeColsWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * See [Usage] for how to use these functions.
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[takeWhile][ColumnSet.takeWhile]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeWhile][SingleColumn.takeColsWhile]` { it.`[any][org.jetbrains.kotlinx.dataframe.DataColumn.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeColsWhile][String.takeColsWhile]` { it.`[kind][org.jetbrains.kotlinx.dataframe.DataColumn.kind]`() == `[ColumnKind.Value][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[takeWhile][ColumnsSelectionDsl.takeWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first columns adhering to the [predicate].
     */
    public fun ColumnsSelectionDsl<*>.takeWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        asSingleColumn().takeColsWhile(predicate)

    /**
     * ## Take (Cols) While
     * This function takes the first columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or
     * [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] adhering to the given [predicate].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [takeWhile][SingleColumn.takeColsWhile] will take the
     * first cols of that column group adhering to the given [predicate].
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [takeWhile][ColumnSet.takeWhile] will
     * take the first columns of that column set adhering to the given [predicate].
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeWhile` is called
     * `takeColsWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * See [Usage] for how to use these functions.
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[takeWhile][ColumnSet.takeWhile]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeWhile][SingleColumn.takeColsWhile]` { it.`[any][org.jetbrains.kotlinx.dataframe.DataColumn.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeColsWhile][String.takeColsWhile]` { it.`[kind][org.jetbrains.kotlinx.dataframe.DataColumn.kind]`() == `[ColumnKind.Value][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[takeWhile][SingleColumn.takeColsWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first columns adhering to the [predicate].
     */
    public fun SingleColumn<DataRow<*>>.takeColsWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        this.ensureIsColumnGroup().transformSingle { it.cols().takeWhile(predicate) }

    /**
     * ## Take (Cols) While
     * This function takes the first columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or
     * [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] adhering to the given [predicate].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [takeWhile][SingleColumn.takeColsWhile] will take the
     * first cols of that column group adhering to the given [predicate].
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [takeWhile][ColumnSet.takeWhile] will
     * take the first columns of that column set adhering to the given [predicate].
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeWhile` is called
     * `takeColsWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * See [Usage] for how to use these functions.
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[takeWhile][ColumnSet.takeWhile]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeWhile][SingleColumn.takeColsWhile]` { it.`[any][org.jetbrains.kotlinx.dataframe.DataColumn.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeColsWhile][String.takeColsWhile]` { it.`[kind][org.jetbrains.kotlinx.dataframe.DataColumn.kind]`() == `[ColumnKind.Value][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[takeColsWhile][String.takeColsWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first columns adhering to the [predicate].
     */
    public fun String.takeColsWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        columnGroup(this).takeColsWhile(predicate)

    /**
     * ## Take (Cols) While
     * This function takes the first columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or
     * [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] adhering to the given [predicate].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [takeWhile][SingleColumn.takeColsWhile] will take the
     * first cols of that column group adhering to the given [predicate].
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [takeWhile][ColumnSet.takeWhile] will
     * take the first columns of that column set adhering to the given [predicate].
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeWhile` is called
     * `takeColsWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * See [Usage] for how to use these functions.
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[takeWhile][ColumnSet.takeWhile]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeWhile][SingleColumn.takeColsWhile]` { it.`[any][org.jetbrains.kotlinx.dataframe.DataColumn.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeColsWhile][String.takeColsWhile]` { it.`[kind][org.jetbrains.kotlinx.dataframe.DataColumn.kind]`() == `[ColumnKind.Value][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[takeColsWhile][SingleColumn.takeColsWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[takeColsWhile][KProperty.takeColsWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first columns adhering to the [predicate].
     */
    public fun KProperty<*>.takeColsWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        columnGroup(this).takeColsWhile(predicate)

    /**
     * ## Take (Cols) While
     * This function takes the first columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or
     * [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] adhering to the given [predicate].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [takeWhile][SingleColumn.takeColsWhile] will take the
     * first cols of that column group adhering to the given [predicate].
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [takeWhile][ColumnSet.takeWhile] will
     * take the first columns of that column set adhering to the given [predicate].
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeWhile` is called
     * `takeColsWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * See [Usage] for how to use these functions.
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[takeWhile][ColumnSet.takeWhile]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeWhile][SingleColumn.takeColsWhile]` { it.`[any][org.jetbrains.kotlinx.dataframe.DataColumn.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeColsWhile][String.takeColsWhile]` { it.`[kind][org.jetbrains.kotlinx.dataframe.DataColumn.kind]`() == `[ColumnKind.Value][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[takeColsWhile][ColumnPath.takeColsWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first columns adhering to the [predicate].
     */
    public fun ColumnPath.takeColsWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        columnGroup(this).takeColsWhile(predicate)

    // endregion

    // region takeLastWhile

    /**
     * ## Take Last (Cols) While
     * This function takes the last columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or
     * [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] adhering to the given [predicate].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [takeLastWhile][SingleColumn.takeLastColsWhile] will take the
     * last cols of that column group adhering to the given [predicate].
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [takeLastWhile][ColumnSet.takeLastWhile] will
     * take the last columns of that column set adhering to the given [predicate].
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeLastWhile` is called
     * `takeLastColsWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * See [Usage] for how to use these functions.
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[takeLastWhile][ColumnSet.takeLastWhile]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeLastWhile][SingleColumn.takeLastColsWhile]` { it.`[any][org.jetbrains.kotlinx.dataframe.DataColumn.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeLastColsWhile][String.takeLastColsWhile]` { it.`[kind][org.jetbrains.kotlinx.dataframe.DataColumn.kind]`() == `[ColumnKind.Value][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [CommonTakeAndDropWhileDocs.ExampleArg][org.jetbrains.kotlinx.dataframe.documentation.CommonTakeAndDropWhileDocs.ExampleArg]}
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last columns adhering to the [predicate].
     */
    private interface CommonTakeLastWhileDocs

    /**
     * ## Take Last (Cols) While
     * This function takes the last columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or
     * [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] adhering to the given [predicate].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [takeLastWhile][SingleColumn.takeLastColsWhile] will take the
     * last cols of that column group adhering to the given [predicate].
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [takeLastWhile][ColumnSet.takeLastWhile] will
     * take the last columns of that column set adhering to the given [predicate].
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeLastWhile` is called
     * `takeLastColsWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * See [Usage] for how to use these functions.
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[takeLastWhile][ColumnSet.takeLastWhile]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeLastWhile][SingleColumn.takeLastColsWhile]` { it.`[any][org.jetbrains.kotlinx.dataframe.DataColumn.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeLastColsWhile][String.takeLastColsWhile]` { it.`[kind][org.jetbrains.kotlinx.dataframe.DataColumn.kind]`() == `[ColumnKind.Value][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[takeLastWhile][ColumnSet.takeLastWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[takeLastWhile][ColumnSet.takeLastWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last columns adhering to the [predicate].
     */
    public fun <C> ColumnSet<C>.takeLastWhile(predicate: ColumnFilter<C>): ColumnSet<C> =
        transform { it.takeLastWhile(predicate) }

    /**
     * ## Take Last (Cols) While
     * This function takes the last columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or
     * [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] adhering to the given [predicate].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [takeLastWhile][SingleColumn.takeLastColsWhile] will take the
     * last cols of that column group adhering to the given [predicate].
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [takeLastWhile][ColumnSet.takeLastWhile] will
     * take the last columns of that column set adhering to the given [predicate].
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeLastWhile` is called
     * `takeLastColsWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * See [Usage] for how to use these functions.
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[takeLastWhile][ColumnSet.takeLastWhile]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeLastWhile][SingleColumn.takeLastColsWhile]` { it.`[any][org.jetbrains.kotlinx.dataframe.DataColumn.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeLastColsWhile][String.takeLastColsWhile]` { it.`[kind][org.jetbrains.kotlinx.dataframe.DataColumn.kind]`() == `[ColumnKind.Value][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[takeLastWhile][ColumnsSelectionDsl.takeLastWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last columns adhering to the [predicate].
     */
    public fun ColumnsSelectionDsl<*>.takeLastWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        asSingleColumn().takeLastColsWhile(predicate)

    /**
     * ## Take Last (Cols) While
     * This function takes the last columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or
     * [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] adhering to the given [predicate].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [takeLastWhile][SingleColumn.takeLastColsWhile] will take the
     * last cols of that column group adhering to the given [predicate].
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [takeLastWhile][ColumnSet.takeLastWhile] will
     * take the last columns of that column set adhering to the given [predicate].
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeLastWhile` is called
     * `takeLastColsWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * See [Usage] for how to use these functions.
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[takeLastWhile][ColumnSet.takeLastWhile]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeLastWhile][SingleColumn.takeLastColsWhile]` { it.`[any][org.jetbrains.kotlinx.dataframe.DataColumn.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeLastColsWhile][String.takeLastColsWhile]` { it.`[kind][org.jetbrains.kotlinx.dataframe.DataColumn.kind]`() == `[ColumnKind.Value][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[takeLastColsWhile][SingleColumn.takeLastColsWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last columns adhering to the [predicate].
     */
    public fun SingleColumn<DataRow<*>>.takeLastColsWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        this.ensureIsColumnGroup().transformSingle { it.cols().takeLastWhile(predicate) }

    /**
     * ## Take Last (Cols) While
     * This function takes the last columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or
     * [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] adhering to the given [predicate].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [takeLastWhile][SingleColumn.takeLastColsWhile] will take the
     * last cols of that column group adhering to the given [predicate].
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [takeLastWhile][ColumnSet.takeLastWhile] will
     * take the last columns of that column set adhering to the given [predicate].
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeLastWhile` is called
     * `takeLastColsWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * See [Usage] for how to use these functions.
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[takeLastWhile][ColumnSet.takeLastWhile]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeLastWhile][SingleColumn.takeLastColsWhile]` { it.`[any][org.jetbrains.kotlinx.dataframe.DataColumn.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeLastColsWhile][String.takeLastColsWhile]` { it.`[kind][org.jetbrains.kotlinx.dataframe.DataColumn.kind]`() == `[ColumnKind.Value][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[takeLastColsWhile][String.takeLastColsWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last columns adhering to the [predicate].
     */
    public fun String.takeLastColsWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        columnGroup(this).takeLastColsWhile(predicate)

    /**
     * ## Take Last (Cols) While
     * This function takes the last columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or
     * [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] adhering to the given [predicate].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [takeLastWhile][SingleColumn.takeLastColsWhile] will take the
     * last cols of that column group adhering to the given [predicate].
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [takeLastWhile][ColumnSet.takeLastWhile] will
     * take the last columns of that column set adhering to the given [predicate].
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeLastWhile` is called
     * `takeLastColsWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * See [Usage] for how to use these functions.
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[takeLastWhile][ColumnSet.takeLastWhile]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeLastWhile][SingleColumn.takeLastColsWhile]` { it.`[any][org.jetbrains.kotlinx.dataframe.DataColumn.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeLastColsWhile][String.takeLastColsWhile]` { it.`[kind][org.jetbrains.kotlinx.dataframe.DataColumn.kind]`() == `[ColumnKind.Value][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[takeLastColsWhile][SingleColumn.takeLastColsWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[takeLastColsWhile][KProperty.takeLastColsWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last columns adhering to the [predicate].
     */
    public fun KProperty<*>.takeLastColsWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        columnGroup(this).takeLastColsWhile(predicate)

    /**
     * ## Take Last (Cols) While
     * This function takes the last columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or
     * [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] adhering to the given [predicate].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [takeLastWhile][SingleColumn.takeLastColsWhile] will take the
     * last cols of that column group adhering to the given [predicate].
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [takeLastWhile][ColumnSet.takeLastWhile] will
     * take the last columns of that column set adhering to the given [predicate].
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeLastWhile` is called
     * `takeLastColsWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * See [Usage] for how to use these functions.
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[takeLastWhile][ColumnSet.takeLastWhile]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeLastWhile][SingleColumn.takeLastColsWhile]` { it.`[any][org.jetbrains.kotlinx.dataframe.DataColumn.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeLastColsWhile][String.takeLastColsWhile]` { it.`[kind][org.jetbrains.kotlinx.dataframe.DataColumn.kind]`() == `[ColumnKind.Value][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[takeLastColsWhile][ColumnPath.takeLastColsWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last columns adhering to the [predicate].
     */
    public fun ColumnPath.takeLastColsWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        columnGroup(this).takeLastColsWhile(predicate)

    // endregion
}

// endregion
