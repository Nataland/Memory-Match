package games.nataland.memorymatch.main

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import games.nataland.memorymatch.R
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test

class MainActivityTest {

    @get:Rule
    var activityRule: ActivityTestRule<MainActivity>
            = ActivityTestRule(MainActivity::class.java)

    @Test
    fun clickingStartHidesStartButtonAndShowsLevel() {
        onView(withId(R.id.start_button)).perform(click())
        onView(withId(R.id.start_button)).check(matches(not(isDisplayed())))
        onView(withId(R.id.level)).check(matches(isDisplayed()))
    }
}