[//]: # (title: Create)

## Columns
<!---docs.Base.CreateColumns.namedColumnWitValues-->
```kotlin
val name by columnOf("Alice", "Bob")
val col = listOf("Alice", "Bob").toColumn("name")
```
<!---END-->
### Column accessors
<!---docs.Base.CreateColumns.namedColumnWithoutValues-->
```kotlin
val name by column<String>()
val col = column<String>("name")
```
<!---END-->
Named column without values is called `ColumnAccessor` and can be used in `DataFrame` operations for typed access to columns:
<!---docs.Base.CreateColumns.colRefForTypedAccess-->
```kotlin
df.filter { it[name].startsWith("A") }
df.sortBy { col }
```
<!---END-->
`ColumnReference` can be converted to `DataColumn` by adding values:
```kotlin
val col = name.withValues("Alice", "Bob")
```
or for `Iterable` of values:
```kotlin
val values = listOf("Alice", "Bob")
val col = name.withValues(values)
val col = values.toColumn(name)
```
### Unnamed column with values
<!---docs.Base.CreateColumns.unnamedColumnWithValues-->
```kotlin
val cols = columnOf("Alice", "Bob")
val colsFromList = listOf("Alice", "Bob").toColumn()
```
<!---END-->
To rename column use function `rename` or infix function `named`:
<!---docs.Base.CreateColumns.namedAndRenameCol-->
```kotlin
val unnamedCol = columnOf("Alice", "Bob")
val colRename = unnamedCol.rename("name")
val colNamed = columnOf("Alice", "Bob") named "name"
```
<!---END-->

## DataFrame

Several ways to convert data into `DataFrame`
### from values
2 columns and 3 rows:
```kotlin
val df = dataFrameOf("name", "age")(
   "Alice", 15,
   "Bob", 20,
   "Mark", 100
)
```
Columns from 'a' to 'z', values from 1 to 10 for each column:
```kotlin
val df = dataFrameOf('a'..'z') { 1..10 }
```
Columns from 1 to 5 filled with 7 random double values each:
```kotlin
val df = dataFrameOf(1..5).randomDouble(7)
```
Column names from list, fill each column with 15 'true' values:
```kotlin
val names = listOf("first", "second", "third")
val df = dataFrameOf(names).fill(15, true)
```

### from columns
`DataFrame` can be created from one or several [columns](#columns)

```kotlin
val name by columnOf("Alice", "Bob")
val age by columnOf(15, 20)

val df1 = dataFrameOf(name, age)
val df2 = listOf(name, age).toDataFrame()
val df3 = name + age
```
### from map
`Map<String, Iterable<Any?>>` can be converted to `DataFrame`:
```kotlin
val data = mapOf("name" to listOf("Alice", "Bob"), "age" to listOf(15, 20))
val df = data.toDataFrame()
```
### from objects

DataFrame can be created from a list of any objects.
Assume we have a list of `Person` objects:
```kotlin
data class Person(val name: String, val age: Int)
val persons = listOf(Person("Alice", 15), Person("Bob", 20))
```
This list can be converted to `DataFrame` with columns for every public property of `Person` class:
```kotlin
persons.toDataFrameByProperties()
```

name | age
---|---
Alice | 15
Bob | 20

You can also specify custom expressions for every column:
```kotlin
val df = persons.toDataFrame {
   "name" { name }
   "year of birth" { 2021 - age }
}
```

name | year of birth
---|---
Alice | 2006
Bob | 2001