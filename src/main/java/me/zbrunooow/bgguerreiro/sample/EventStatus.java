package me.zbrunooow.bgguerreiro.sample;

public enum EventStatus {
  OFF,
  STARTING,
  ANNOUNCING,
  WAITING,
  STARTED,
  DEATHMATCH,
  FINALIZED;

  public static EventStatus withString(String string) {
    for (EventStatus status : values()) {
      if (status.name().equalsIgnoreCase(string)) {
        return status;
      }
    }
    return OFF;
  }

}
