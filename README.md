# GradleKotlinUtils

This is a plugin for gradle settings and projects, that's means that you can apply it in `settings.gradle.kts` and `build.gradle.kts`

## Usage

### Apply in settings.gradle.kts

```kotlin
plugins {
    id("io.github.wuseal.utils") version "1.0.0"
}
```

### Apply in build.gradle.kts

```kotlin
plugins {
    id("io.github.wuseal.utils") version "1.0.0"
}
```

### Ability
After apply this plugin, you can 

* Use Gson library to do json serialize and deserialize by Gson library in `settings.gradle.kts` and `build.gradle.kts`
* Use runCommand and evalBash funtion to execute eternal shell command in `settings.gradle.kts and` and `build.gradle.kts`
* Use pre functions in any `*.gradle.kts` that applied in settings.gradle.kts and build.gradle.kts

### Demos
Gson demo
```kotlin
Gson().toJson(Pair(123, 456))

prettyJsonGson.fromJson<Pair<Int, Int>>("{\"first\":1,\"second\":2}")
```

Command Run demo

```kotlin
"ls".runCommand()

println("cat /Users/user/Scripts/TempGradleProject/build.gradle.kts".evalBash())

```

In other gradle.kts that applied in settings.gradle.kts or build.gradle.kts

Firstly, declare the functions in `*.gradle.kts`
```kotlin
val fromJson: String.(Class<*>) -> Any by extensions
val toJson: Any.() -> String by extensions
val runCommand: String.() -> Result<Unit> by extensions
val evalBash: String.() -> Result<String> by extensions
```
And then use them in `*.gradle.kts`
```kotlin
val fromJsonObj = "{\"first\":1,\"second\":2}".fromJson(typeOf<Pair<String, String>>().concreteClass)
println(fromJsonObj.toString())
(fromJsonObj as Pair<*, *>).run {
    assert(first is String)
    assert(second is String)
    println("print form typeof" + this.toString())
}
"cd ~ && ls".runCommand()
println("echo \$GITHUB_USER_NAME".evalBash().getOrThrow())
```