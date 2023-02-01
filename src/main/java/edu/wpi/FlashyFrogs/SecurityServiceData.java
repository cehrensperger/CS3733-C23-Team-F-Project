package edu.wpi.FlashyFrogs;

public class SecurityServiceData {
  private String incidentReportEntry;
  private String locationEntry;
  private String dateEntry;
  private String timeEntry;
  private String firstEntry;
  private String middleEntry;
  private String lastEntry;
  private String departmentEntry;

  /**
   * sets the data from the security service form
   *
   * @param incidentReportEntry
   * @param locationEntry
   * @param dateEntry
   * @param timeEntry
   * @param firstEntry
   * @param middleEntry
   * @param lastEntry
   * @param departmentEntry
   */
  public void setInfo(
      String incidentReportEntry,
      String locationEntry,
      String dateEntry,
      String timeEntry,
      String firstEntry,
      String middleEntry,
      String lastEntry,
      String departmentEntry) {
    this.incidentReportEntry = incidentReportEntry;
    this.locationEntry = locationEntry;
    this.dateEntry = dateEntry;
    this.timeEntry = timeEntry;
    this.firstEntry = firstEntry;
    this.middleEntry = middleEntry;
    this.lastEntry = lastEntry;
    this.departmentEntry = departmentEntry;
  }

  /**
   * prints the data from teh security service form
   *
   * @return
   */
  public String getInfo() {
    return (incidentReportEntry
        + "\n"
        + locationEntry
        + "\n"
        + dateEntry
        + "\n"
        + timeEntry
        + "\n"
        + firstEntry
        + "\n"
        + middleEntry
        + "\n"
        + lastEntry
        + "\n"
        + departmentEntry);
  }
}
