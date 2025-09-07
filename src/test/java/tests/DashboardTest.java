package tests;

import org.testng.Assert;
import org.testng.annotations.Test;

public class DashboardTest extends BaseTest {

    @Test(dependsOnGroups = "login")
    public void testTilesAndCounts() {
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
        int ncTileCount = dashboardPage().getTileCount(dashboardPage().getNonConsentedTileCountSelector());
        System.out.println("NonConsented Tile Count (Total Patients): " + ncTileCount);
        int ncRowCount = dashboardPage().clickTileAndCount(
                dashboardPage().getNonConsentedTileClickSelector(),
                dashboardPage().getPatientRowsSelector(),
                dashboardPage().getNextButtonSelector());
        System.out.println("Non‑Consented Row Count: " + ncRowCount);
        Assert.assertEquals(ncRowCount, ncTileCount, "Row count should match non-consented tile count");
    }
}