package tests;

import org.testng.Assert;
import org.testng.annotations.Test;

public class DashboardTest extends BaseTest {

    @Test(dependsOnMethods = "tests.LoginTest.testLogin")
    public void testTilesAndCounts() {
        var test = extent.createTest("Dashboard Tiles and Patient List Counts");
        loginPage().navigate();
        loginPage().login("carlos@test.com", "Admin@456");

        // Total Patients
        int tileCount = dashboardPage().getTileCount(dashboardPage().getTotalPatientsTileCountSelector());
        System.out.println("Total Patients Tile Count (Total Patients): " + tileCount);

        int rowCount = dashboardPage().clickTileAndCount(
                dashboardPage().getTotalPatientsTileClickSelector(),
                dashboardPage().getPatientRowsSelector(),
                dashboardPage().getNextButtonSelector()
        );
        System.out.println("Row Count (Patient List): " + rowCount);

        // Assertion
        Assert.assertEquals(rowCount, tileCount, "Row count must match tile count");

        // Non‑Consented But Eligible
        //dashboardPage().clickLogo(); // navigate home
        int ncTileCount = dashboardPage().getTileCount(dashboardPage().getNonConsentedTileCountSelector());
        System.out.println("NonConsented Tile Count (Total Patients): " + tileCount);
        int ncRowCount = dashboardPage().clickTileAndCount(dashboardPage().getNonConsentedTileClickSelector(),dashboardPage().getPatientRowsSelector(),
                dashboardPage().getNextButtonSelector());
        test.pass("Non‑Consented But Eligible tile count: " + ncTileCount);
        Assert.assertEquals(ncRowCount, ncTileCount, "Row count should match non-consented tile count");
    }
}