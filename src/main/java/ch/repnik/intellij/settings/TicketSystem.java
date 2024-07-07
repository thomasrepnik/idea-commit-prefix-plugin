package ch.repnik.intellij.settings;

import java.util.Arrays;

public enum TicketSystem {
  JIRA("Jira (ABC-1234)"),OTHER("Other (12345)");

  private final String description;

  TicketSystem(String description) {
    this.description = description;
  }

  public String getDescription() {
    return this.description;
  }

  public static TicketSystem parse(String description) {
    return Arrays.stream(TicketSystem.values())
        .filter(ticketSystem -> ticketSystem.getDescription().equals(description))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException(
            "Description '" + description + "' was not found in enum"));
  }
}
