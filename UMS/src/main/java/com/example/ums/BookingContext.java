package com.example.ums;

public class BookingContext {

        private static int selectedHallId;

        public static void setSelectedHallId(int hallId) {
            selectedHallId = hallId;
        }

        public static int getSelectedHallId() {
            return selectedHallId;
        }
    }

