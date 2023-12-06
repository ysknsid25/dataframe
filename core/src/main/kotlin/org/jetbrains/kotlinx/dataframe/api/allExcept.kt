package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.UnresolvedColumnsPolicy
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate
import org.jetbrains.kotlinx.dataframe.impl.aggregation.toColumns
import org.jetbrains.kotlinx.dataframe.impl.columns.allColumnsExceptAndUnpack
import org.jetbrains.kotlinx.dataframe.impl.columns.allColumnsExceptKeepingStructure
import org.jetbrains.kotlinx.dataframe.impl.columns.changePath
import org.jetbrains.kotlinx.dataframe.impl.columns.createColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.isMissingColumn
import org.jetbrains.kotlinx.dataframe.impl.columns.transformSingle
import org.jetbrains.kotlinx.dataframe.impl.getColumnsWithPaths
import kotlin.reflect.KProperty

// region ColumnsSelectionDsl

public interface AllExceptColumnsSelectionDsl<out T> {

    // region except

    /**
     * ## All (Except) Usage
     *
     * @include [UsageTemplate]
     * {@setArg [UsageTemplate.DefinitionsArg]
     *  {@include [UsageTemplate.ColumnSetDef]}
     *  {@include [LineBreak]}
     *  {@include [UsageTemplate.ColumnGroupDef]}
     *  {@include [LineBreak]}
     *  {@include [UsageTemplate.ConditionDef]}
     * }
     *
     *
     */
    public interface Usage

//    /** TODO tbd */
//    @Suppress("UNCHECKED_CAST")
//    public fun <C> ColumnSet<C>.colsExcept(predicate: ColumnFilter<C>): TransformableColumnSet<C> =
//        colsInternal { !predicate(it as ColumnWithPath<C>) } as TransformableColumnSet<C>
//
//    /** TODO tbd */
//    public fun SingleColumn<DataRow<*>>.colsExcept(predicate: ColumnFilter<*>): TransformableColumnSet<*> =
//        colsInternal { !predicate(it) }

    // TODO Same as select and cols but then inverted

    // region deprecated and experiments

    public operator fun ColumnReference<*>.not(): ColumnSet<Any?> =
        with(this@AllExceptColumnsSelectionDsl as ColumnsSelectionDsl<T>) {
            allExcept(this@not)
        }

    public operator fun ColumnSet<*>.not(): ColumnSet<Any?> =
        with(this@AllExceptColumnsSelectionDsl as ColumnsSelectionDsl<T>) {
            allExcept(this@not)
        }

    public infix fun <C> ColumnSet<C>.oldExcept(other: ColumnsResolver<*>): ColumnSet<C> =
        createColumnSet { context ->
            val resolvedCols = this@oldExcept.resolve(context)
            val resolvedColsToExcept = other.resolve(context)
            resolvedCols.allColumnsExceptAndUnpack(resolvedColsToExcept)
        } as ColumnSet<C>

    // TODO TBD
    @Deprecated("Use allExcept instead", ReplaceWith("this.allColsExcept(selector)"), DeprecationLevel.WARNING)
    public infix fun <C> SingleColumn<DataRow<C>>.except(selector: ColumnsSelector<C, *>): ColumnSet<*> =
        allColsExcept(selector)

    @Deprecated("Use allExcept instead", ReplaceWith("this.allExcept(selector)"), DeprecationLevel.WARNING)
    public infix fun <C> ColumnsSelectionDsl<C>.except(selector: ColumnsSelector<C, *>): ColumnSet<*> =
        allExcept(selector)

    public infix fun <C> SingleColumn<DataRow<C>>.exceptNew(selector: ColumnsSelector<C, *>): SingleColumn<DataRow<*>> =
        this.ensureIsColumnGroup().transformSingle { singleCol ->

            val columnsToExcept = singleCol.asColumnGroup().getColumnsWithPaths(selector)
                .map { it.changePath(singleCol.path + it.path) }

            val newCols = listOf(singleCol).allColumnsExceptKeepingStructure(columnsToExcept)

            newCols as List<ColumnWithPath<DataRow<*>>>
        }.singleInternal()

    @Deprecated("Use allExcept instead", ReplaceWith("this.allExcept(other)"))
    public fun SingleColumn<DataRow<*>>.except(vararg other: ColumnsResolver<*>): ColumnSet<*> =
        allColsExcept(*other)

    @Deprecated("Use allExcept instead", ReplaceWith("this.allExcept(other)"))
    public fun ColumnsSelectionDsl<*>.except(vararg other: ColumnsResolver<*>): ColumnSet<*> =
        allExcept(*other)

    /** TODO tbd */
    public operator fun SingleColumn<DataRow<*>>.minus(other: ColumnsResolver<*>): ColumnSet<*> =
        allColsExcept(other)

    /** TODO tbd */
    public operator fun <C> SingleColumn<DataRow<C>>.minus(selector: ColumnsSelector<C, *>): ColumnSet<*> =
        allColsExcept(selector)

    // endregion

    // region ColumnSet

    public infix fun <C> ColumnSet<C>.except(selector: () -> ColumnsResolver<*>): ColumnSet<C> =
//        except(selector.toColumns<Any?, _>())
        except(selector())

    public infix fun <C> ColumnSet<C>.except(other: ColumnsResolver<*>): ColumnSet<C> =
        createColumnSet { context ->
            val resolvedCols = this@except.resolve(context)
            val resolvedColsToExcept = other.resolve(context)
            resolvedCols.allColumnsExceptKeepingStructure(resolvedColsToExcept)
        } as ColumnSet<C>

    public fun <C> ColumnSet<C>.except(vararg other: ColumnsResolver<*>): ColumnSet<C> =
        except(other.toColumnSet())

    public infix fun <C> ColumnSet<C>.except(other: String): ColumnSet<C> =
        except(column<Any?>(other))

    public fun <C> ColumnSet<C>.except(vararg others: String): ColumnSet<C> =
        except(others.toColumnSet())

    public infix fun <C> ColumnSet<C>.except(other: KProperty<C>): ColumnSet<C> =
        except(column(other))

    public fun <C> ColumnSet<C>.except(vararg others: KProperty<C>): ColumnSet<C> =
        except(others.toColumnSet())

    public infix fun <C> ColumnSet<C>.except(other: ColumnPath): ColumnSet<C> =
        except(column<Any?>(other))

    public fun <C> ColumnSet<C>.except(vararg others: ColumnPath): ColumnSet<C> =
        except(others.toColumnSet())

    // endregion

    // region ColumnsSelectionDsl

    public fun <C> ColumnsSelectionDsl<C>.allExcept(selector: ColumnsSelector<C, *>): ColumnSet<*> =
        this.asSingleColumn().allColsExcept(selector)

    public fun ColumnsSelectionDsl<*>.allExcept(vararg other: ColumnsResolver<*>): ColumnSet<*> =
        asSingleColumn().allColsExcept(other.toColumnSet())

    public fun ColumnsSelectionDsl<*>.allExcept(vararg others: String): ColumnSet<*> =
        asSingleColumn().allColsExcept(others.toColumnSet())

    public fun ColumnsSelectionDsl<*>.allExcept(vararg others: KProperty<*>): ColumnSet<*> =
        asSingleColumn().allColsExcept(others.toColumnSet())

    public fun ColumnsSelectionDsl<*>.allExcept(vararg others: ColumnPath): ColumnSet<*> =
        asSingleColumn().allColsExcept(others.toColumnSet())

    // endregion

    // region SingleColumn

    public infix fun <C> SingleColumn<DataRow<C>>.allColsExcept(selector: ColumnsSelector<C, *>): ColumnSet<*> =
        allColsExceptInternal(selector.toColumns())

    public infix fun SingleColumn<DataRow<*>>.allColsExcept(other: ColumnsResolver<*>): ColumnSet<*> =
        allColsExceptInternal(other)

    public fun SingleColumn<DataRow<*>>.allColsExcept(vararg other: ColumnsResolver<*>): ColumnSet<*> =
        allColsExcept(other.toColumnSet())

    public infix fun SingleColumn<DataRow<*>>.allColsExcept(other: String): ColumnSet<*> =
        allColsExcept(column<Any?>(other))

    public fun SingleColumn<DataRow<*>>.allColsExcept(vararg others: String): ColumnSet<*> =
        allColsExcept(others.toColumnSet())

    public infix fun SingleColumn<DataRow<*>>.allColsExcept(other: KProperty<*>): ColumnSet<*> =
        allColsExcept(column(other))

    public fun SingleColumn<DataRow<*>>.allColsExcept(vararg others: KProperty<*>): ColumnSet<*> =
        allColsExcept(others.toColumnSet())

    // reference and path
    public infix fun SingleColumn<DataRow<*>>.allColsExcept(other: ColumnReference<*>): ColumnSet<*> =
        allColsExceptInternal(other)

    public fun SingleColumn<DataRow<*>>.allColsExcept(vararg other: ColumnReference<*>): ColumnSet<*> =
        allColsExceptInternal(*other)

    // endregion

    // region String

    public fun String.allColsExcept(selector: ColumnsSelector<*, *>): ColumnSet<*> =
        columnGroup(this).allColsExcept(selector)

    public infix fun String.allColsExcept(other: ColumnsResolver<*>): ColumnSet<*> =
        columnGroup(this).allColsExcept(other)

    public fun String.allColsExcept(vararg others: ColumnsResolver<*>): ColumnSet<*> =
        allColsExcept(others.toColumnSet())

    public fun String.allColsExcept(other: String): ColumnSet<*> =
        columnGroup(this).allColsExcept(other)

    public fun String.allColsExcept(vararg others: String): ColumnSet<*> =
        allColsExcept(others.toColumnSet())

    public fun String.allColsExcept(other: KProperty<*>): ColumnSet<*> =
        columnGroup(this).allColsExcept(other)

    public fun String.allColsExcept(vararg others: KProperty<*>): ColumnSet<*> =
        allColsExcept(others.toColumnSet())

    public fun String.allColsExcept(other: ColumnReference<*>): ColumnSet<*> =
        columnGroup(this).allColsExcept(other)

    public fun String.allColsExcept(vararg others: ColumnReference<*>): ColumnSet<*> =
        columnGroup(this).allColsExcept(*others)

    // endregion

    // region KProperty

    public fun <C> KProperty<DataRow<C>>.allColsExcept(selector: ColumnsSelector<C, *>): ColumnSet<*> =
        columnGroup(this).allColsExcept(selector)

    public infix fun KProperty<*>.allColsExcept(other: ColumnsResolver<*>): ColumnSet<*> =
        columnGroup(this).allColsExcept(other)

    public fun KProperty<*>.allColsExcept(vararg others: ColumnsResolver<*>): ColumnSet<*> =
        allColsExcept(others.toColumnSet())

    public infix fun KProperty<*>.allColsExcept(other: String): ColumnSet<*> =
        columnGroup(this).allColsExcept(other)

    public fun KProperty<*>.allColsExcept(vararg others: String): ColumnSet<*> =
        allColsExcept(others.toColumnSet())

    public infix fun KProperty<*>.allColsExcept(other: KProperty<*>): ColumnSet<*> =
        columnGroup(this).allColsExcept(other)

    public fun KProperty<*>.allColsExcept(vararg others: KProperty<*>): ColumnSet<*> =
        allColsExcept(others.toColumnSet())

    public infix fun KProperty<*>.allColsExcept(other: ColumnReference<*>): ColumnSet<*> =
        columnGroup(this).allColsExcept(other)

    public fun KProperty<*>.allColsExcept(vararg others: ColumnReference<*>): ColumnSet<*> =
        columnGroup(this).allColsExcept(*others)

    // endregion

    // region ColumnPath

    public fun ColumnPath.allColsExcept(selector: ColumnsSelector<*, *>): ColumnSet<*> =
        columnGroup(this).allColsExcept(selector)

    public infix fun ColumnPath.allColsExcept(other: ColumnsResolver<*>): ColumnSet<*> =
        columnGroup(this).allColsExcept(other)

    public fun ColumnPath.allColsExcept(vararg others: ColumnsResolver<*>): ColumnSet<*> =
        allColsExcept(others.toColumnSet())

    public infix fun ColumnPath.allColsExcept(other: String): ColumnSet<*> =
        columnGroup(this).allColsExcept(other)

    public fun ColumnPath.allColsExcept(vararg others: String): ColumnSet<*> =
        allColsExcept(others.toColumnSet())

    public infix fun ColumnPath.allColsExcept(other: KProperty<*>): ColumnSet<*> =
        columnGroup(this).allColsExcept(other)

    public fun ColumnPath.allColsExcept(vararg others: KProperty<*>): ColumnSet<*> =
        allColsExcept(others.toColumnSet())

    public infix fun ColumnPath.allColsExcept(other: ColumnReference<*>): ColumnSet<*> =
        columnGroup(this).allColsExcept(other)

    public fun ColumnPath.allColsExcept(vararg others: ColumnReference<*>): ColumnSet<*> =
        columnGroup(this).allColsExcept(*others)

    // endregion

    // endregion

    /**
     * streamlines column references such that both relative and absolute paths can be used
     */
    // TODO remove this overload again
    private fun SingleColumn<DataRow<*>>.allColsExceptInternal(vararg others: ColumnReference<*>): ColumnSet<*> =
        allColsExceptInternal(others.toColumnSet())
    //        transformSingleWithContext { col ->
//            val correctedOthers = others.map {
//               it.path().dropStartWrt(col.path)
//            }
//            allColsExceptInternal(correctedOthers.toColumnSet()).resolve(this)
//        }

    private fun SingleColumn<DataRow<*>>.allColsExceptInternal(other: ColumnsResolver<*>) =
        createColumnSet { context ->
            val col = this.ensureIsColumnGroup().resolveSingle(context)
                ?: return@createColumnSet emptyList()
            val colGroup = col.asColumnGroup()
            val colPath = col.path

            val parentScope = (this@AllExceptColumnsSelectionDsl as ColumnsSelectionDsl<T>)
                .asSingleColumn()
            val parentCol = parentScope.ensureIsColumnGroup().resolveSingle(context)
                ?: return@createColumnSet emptyList()
            val parentColGroup = parentCol.asColumnGroup()
            val parentPath = parentCol.path

            val allCols = colGroup.getColumnsWithPaths { all() }

            val colsToExceptRelativeToParent = parentColGroup
                .getColumnsWithPaths(UnresolvedColumnsPolicy.Skip) { other }


            val colsToExceptRelativeToCol = colGroup
                .getColumnsWithPaths(UnresolvedColumnsPolicy.Skip) { other }

            // throw exceptions for columns that weren't in this or parent scope
            (colsToExceptRelativeToParent + colsToExceptRelativeToCol).groupBy { it.path }
                .forEach { (path, cols) ->
                    if (cols.all { it.data.isMissingColumn() }) {
                        throw IllegalArgumentException(
                            "Column ${(colPath + path).joinToString()} and ${(parentPath + path).joinToString()} not found."
                        )
                    }
                }

            val colsToExcept = colsToExceptRelativeToCol +
                colsToExceptRelativeToParent.map { // adjust the path to be relative to the current column
                    it.changePath(it.path.dropFirst(colPath.size - parentPath.size))
                }

            allCols.allColumnsExceptKeepingStructure(
                colsToExcept
                    .distinctBy { it.path }
                    .filterNot { it.data.isMissingColumn() }
            ).map { it.changePath(col.path + it.path) }

            // try to resolve all columns to except relative to the current column
//                try {
//                    val columnsToExcept = colGroup
//                        .getColumnsWithPaths(context.unresolvedColumnsPolicy) { other }
//
//                    allCols.allColumnsExceptKeepingStructure(columnsToExcept)
//                        .map { it.changePath(col.path + it.path) }
//                } catch (e: IllegalStateException) {
//                    // if allowed, attempt to resole all columns to except absolutely too if relative failed
//                    if (allowFullPaths) {
//                        val allColsAbsolute = allCols.map { it.addParentPath(col.path) }
//                        val columnsToExcept = other.resolve(context)
//                        allColsAbsolute.allColumnsExceptKeepingStructure(columnsToExcept)
//                    } else {
//                        throw e
//                    }
//                }

        }
}

// endregion
