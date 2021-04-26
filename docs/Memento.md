# Memento
## Overview
Memento makes it easy to persistently store data for your Android app in a SQLite database via the ContentProvider interface.

A ContentProvider is just an abstraction to some underlying data storage mechanism.  The ContentProvider API combines REST and SQL, where data is accessed via URIs (REST-like) and arguments (SQL-like).  Although any storage mechanism could be used, SQLite is common.  ContentProvider is a great abstraction because Android provides many features on top of the ContentProvider interface such as inter-process communication, observer design patterns, permissions, syncing, and multi-threading facilities.

The reason to use this library, rather than roll your own, is that Memento dramatically simplifies the process of implementing and testing a ContentProvider that is DRY, thread-safe, and well tested.  Android ContentProvider design patterns usually combine the business logic and the data model, such that an implementation may have a `UriMatcher` and a repetitive switch statement within each of `query()`, `insert()`, `update()`, and `delete()`.  Within those methods, there are concerns not only about the data model but also SQL injection, permissions, transactions, thread-safety, observer notification, and so on.  Memento, on the other hand, takes care of implementing the business logic so that the developer only has to worry about implementing the data model.  In addition, Memento provides facilities to simplify data model creation.  Memento ensures minimal lock-in: because the app talks to the ContentProvider via the ContentResolver, that layer of indirection makes it easy to remove the dependency on the Memento library in the future.

Additional features include:

* Extensive unit testing, as well as real-world use in top apps on the Google Play Store
* Multi-process and multi-thread safety
* Atomic transactions for [applyBatch(ArrayList)](https://developer.android.com/reference/android/content/ContentProvider.html#applyBatch(java.util.ArrayList<android.content.ContentProviderOperation>)) and [bulkInsert(Uri, ContentValues[])](http://developer.android.com/reference/android/content/ContentProvider.html#bulkInsert(android.net.Uri,%20android.content.ContentValues[]))
* Automatic content change notifications via the ContentResolver as well as via [Intent.ACTION_PROVIDER_CHANGED](http://developer.android.com/reference/android/content/Intent.html#ACTION_PROVIDER_CHANGED) (security for ACTION_PROVIDER_CHANGED is also handled automatically)
* Support for LIMIT clauses via the query parameter [SearchManager.SUGGEST_PARAMETER_LIMIT](https://developer.android.com/reference/android/app/SearchManager.html#SUGGEST_PARAMETER_LIMIT)
* Support for [BaseColumns._COUNT](https://developer.android.com/reference/android/provider/BaseColumns.html#_COUNT) queries
* Enhanced security by ensuring _ID queries are not susceptible to SQL injection



## Usage
### Step by step
1. Define the contract classes for the ContentProvider.  (A contract usually class more or less represents a database table.)
1. Subclass `MementoContentProvider`, providing implementations for:
    1. `SqliteOpenHelper`: Opens and creates the database tables.  To simplify database table creation, consider using the helper classes `SqliteTableBuilder` and `SqliteColumnBuilder`.  Advanced users might also create indexes for improved performance via `SqliteIndexBuilder`.
    1. `SqliteUriMatcher`: Takes Uris and converts them into `SqliteUriMatch` objects that the ContentProvider uses whenever an operation (query, insert, update, delete, etc.) occurs.
1. Create an AndroidManifest entry for the ContentProvider.

### Example
An [example implementation](https://github.com/twofortyfouram/android-monorepo/tree/master/mementoImplLib/src/androidTest/java/com/twofortyfouram/memento/test) exists as part of the test suite.

### Further reading
The official Android [ContentProvider Developer Guide](https://developer.android.com/guide/topics/providers/content-providers.html).

<!--
## API Reference
JavaDocs for the library are published [here](http://twofortyfouram.github.io/android-memento).-->

## Compatibility
The library is compatible and optimized for Android API Level 19 and above.

<!--
## Download
The library is published as an artifact to jCenter.  To use the library, the jCenter repository and the artifact need to be added to your build script.

The build.gradle repositories section would look something like the following:

    repositories {
        jcenter()
    }

And the dependencies section would look something like this:
    
    dependencies {
        implementation group:'com.twofortyfouram', name:'android-memento-api', version:’[1.0.0,2.0[‘
        implementation group:'com.twofortyfouram', name:'android-memento-impl', version:’[1.0.0,2.0[‘
    }

## History
-->
