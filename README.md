# APM (Android Performance Monitoring)

A lightweight Android performance monitoring library that helps you identify and fix performance issues in your app.

## Features

- Thread Sampling: Monitor thread creation and destruction in real-time
- Looper Monitoring: Track main thread execution time and detect UI freezes
- StrictMode Integration: Easily enable StrictMode for development builds
- Logcat Reader: Collect logcat output to help debug non-reproducible issues

## Installation

Add JitPack repository to your build file. Add it in your root build.gradle at the end of repositories:

```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

Add the dependency:

```groovy
dependencies {
    implementation 'com.github.zealot2002:apm:1.0.0'
}
```

For Kotlin DSL:

```kotlin
dependencies {
    implementation("com.github.zealot2002:apm:1.0.0")
}
```

## Usage

The library is automatically initialized via a ContentProvider. You can configure which monitoring features to enable:

```kotlin
// In your Application class or early in app startup
import com.joy.apm.KitSwitch

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Enable the entire library
        KitSwitch.enable = true
        
        // Configure individual features
        KitSwitch.bThreadSampler = true  // Monitor thread creation/destruction
        KitSwitch.bLooperMonitor = true  // Monitor main thread performance
        KitSwitch.bStrictMode = true     // Enable StrictMode
        KitSwitch.bLogcatReader = true   // Collect logcat output
    }
}
```

## Permissions

The library requires the following permissions:

```xml
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_LOGS" tools:ignore="ProtectedPermissions" />
```

## License

```
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
``` 