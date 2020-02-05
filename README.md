[![](https://jitpack.io/v/kdu0136/dot_indicator.svg)](https://jitpack.io/#kdu0136/dot_indicator)

Dot Indicator
=============
A dot indicator compatible with [RecyclerView](https://developer.android.com/reference/android/support/v7/widget/RecyclerView) and [ViewPager](https://developer.android.com/reference/android/support/v4/view/ViewPager) and [ViewPager2](https://developer.android.com/jetpack/androidx/releases/viewpager2).

# Setup

__Step 1.__ Add the JitPack repository to your build file
```groovy
allprojects {
  repositories {
    maven { url 'https://jitpack.io' }
  }
}
```
__Step 2.__ Add the dependency

```groovy
dependencies {
  implementation 'com.github.kdu0136:<latest-version>'
}
```

# Usage

Add the `DotIndicator` to your XML file:

```xml
  <kim.dongun.dotindicator.DotIndicator
      android:id="@+id/dotIndicator"
      android:layout_width="wrap_content"
      android:layout_height="wrap_content" />
```

#### RecyclerView

```kotlin
  dotIndicator attachTo recyclerView
```

#### View Pager

```kotlin
  dotIndicator attachTo viewPager
```

#### View Pager2

```kotlin
  dotIndicator attachTo viewPager2
```

#### Button
```kotlin
  dotIndicator.pageDown()
  dotIndicator.pageUp()
```

# Customization

| Attribute                  | Note                                      | Default     |
|----------------------------|-------------------------------------------|-------------|
| indicatorPadding           | Padding start and end                     | 0dp         |
| dotSpacing                 | Spacing between dots                      | 3dp         |
| dotSelectSize              | Selected dot size                         | 5dp         |
| dotLargeSize               | Unselect large dot size                   | 4.5dp       |
| dotMediumSize              | Unselect medium dot size                  | 3dp         |
| dotSmallSize               | Unselect small dot size                   | 2dp         |
| indicatorAnimDuration      | Duration of animation in ms               | 300         |
| indicatorAnimInterpolator  | Animation interpolator                    | decelerate  |
| dotDefaultColor            | Unselected dot color                      | #E2E2E2     |
| dotSelectedColor           | Selected dot color                        | #000000     |



