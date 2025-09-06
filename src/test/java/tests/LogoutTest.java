package tests;

import org.testng.Assert;
import org.testng.annotations.Test;

public class LogoutTest extends BaseTest {

    @Test(dependsOnMethods = "tests.CommunicationTest.testEmailGroupCreation")
    public void testLogout() {
        var test = extent.createTest("Logout Test");

        loginPage().navigate();
        loginPage().login("carlos@test.com", "Admin@456");
        dashboardPage().click("span.header-profile-user");
        dashboardPage().click("span.align-middle[data-key=\"t-logout\"]");

        boolean loginVisible = loginPage().textContent("button[type='submit']").contains("Sign In");
        Assert.assertTrue(loginVisible, "Login button should be visible after logout");

        test.pass("User logged out and returned to login screen");
    }
}
