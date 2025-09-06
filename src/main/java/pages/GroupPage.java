package pages;

import com.microsoft.playwright.Page;

public class GroupPage extends BasePage {
    private final String groupsMenu = "a.dropdown-item:has-text('Groups')";
    private final String smsGroupTab = "a[role='tab']:has-text('SMS Group')";
    private final String emailGroupTab = "a[role='tab']:has-text('Email Group')";
    private final String groupListRows = "#customerList tbody.group-list tr";
    private final String groupCountColumn = "td:nth-child(2)"; // adjust as needed

    public GroupPage(Page page) {
        super(page);
    }

    public void openSMSGroupList() {
        click(groupsMenu);
        click(smsGroupTab);
    }

    public void openEmailGroupList() {
        click(groupsMenu);
        click(emailGroupTab);
    }

    public int getLatestGroupCount() {
        return Integer.parseInt(textContent(groupListRows + ":last-child " + groupCountColumn).trim());
    }
    public boolean isGroupPresent(String name) {
        return page.locator("text=" + name).isVisible();
    }
}