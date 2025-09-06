package tests;

import com.microsoft.playwright.ElementHandle;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class CommunicationTest extends BaseTest {

    private int lastTileCount;

    @Test(dependsOnMethods = "tests.DashboardTest.testTilesAndCounts")
    public void testSMSGroupCreation() {
        var test = extent.createTest("SMS Group Creation via Dashboard");
        loginPage().navigate();
        loginPage().login("carlos@test.com", "Admin@456");
        int tileCount = dashboardPage().getTileCount(dashboardPage().getTotalPatientsTileCountSelector());
        lastTileCount = tileCount;
        dashboardPage().clickTileAndCount(dashboardPage().getTotalPatientsTileClickSelector(),
                dashboardPage().getPatientRowsSelector(),
                dashboardPage().getNextButtonSelector());
        dashboardPage().selectAllExceptOne();
        dashboardPage().clickCommunication();
        communicationModal().clickSmsTab();
        String groupName = "AutoGroupSMS_" + System.currentTimeMillis();
        communicationModal().newGroup(groupName);
        int patientCount = communicationModal().confirmAndGetCount();
        int expectedCount = tileCount - 1;
        System.out.println("Tile Count: " + tileCount);
        System.out.println("Expected selected: " + expectedCount);
        System.out.println("Popup shows: " + patientCount);
        Assert.assertEquals(patientCount, expectedCount, "Popup count must be tileCount minus one");
        test.pass("Popup shows correct count: " + patientCount);
        dashboardPage().clickProfileImage();
        groupPage().openSMSGroupList();
        int groupCount = groupPage().getLatestGroupCount();
        Assert.assertEquals(groupCount, patientCount , "Group count must match the expected number");
        test.pass("SMS Group created with correct patient count");
    }

    @Test(dependsOnMethods = "testSMSGroupCreation")
    public void testEmailGroupCreation() {
        var test = extent.createTest("Email Group Creation for Eligible Patients");
        dashboardPage().clickLogo(); // navigate home
        int ncTileCount = dashboardPage()
                .getTileCount(dashboardPage().getNonConsentedTileCountSelector());
        System.out.println("Non‑Consented Tile Count: " + ncTileCount);
        int ncRowCount = dashboardPage()
                .clickTileAndCount(
                        dashboardPage().getNonConsentedTileClickSelector(),
                        dashboardPage().getPatientRowsSelector(),
                        dashboardPage().getNextButtonSelector());
        System.out.println("Non‑Consented Row Count: " + ncRowCount);
        Assert.assertEquals(ncRowCount, ncTileCount, "Non-Consented row count must match tile count");

        List<ElementHandle> emailCells = page.querySelectorAll("#patientTable tbody tr td.email");
        int validEmailCount = (int) emailCells.stream()
                .filter(cell -> {
                    try {
                        String email = cell.textContent().trim();
                        return email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
                    } catch (Exception e) {
                        return false;
                    }
                }).count();
        dashboardPage().selectAllExceptOne();
        dashboardPage().clickCommunication();
        communicationModal().clickEmailTab();
        int toCount = communicationModal().getToCount();
        Assert.assertEquals(toCount, validEmailCount, "To field count must match valid email count");
        test.pass("To field has correct recipient count: " + toCount);
        communicationModal().sendEmail("Test Subject", "Test Automation Mail");
        groupPage().openEmailGroupList();
        int emailGroupCount = groupPage().getLatestGroupCount();
        Assert.assertEquals(emailGroupCount, validEmailCount, "Email group count must match valid email count");
        test.pass("Email Group created with correct count");
    }
}