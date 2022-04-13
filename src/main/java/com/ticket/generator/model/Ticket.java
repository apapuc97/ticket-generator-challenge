package com.ticket.generator.model;

public class Ticket {
    private final Strip strip;
    private final int ticketNumber;

    public Ticket(Strip strip, int ticketNumber) {
        this.strip = strip;
        this.ticketNumber = ticketNumber;
    }

    public boolean hasUnfilledColumn(int column) {
        int ticketRow = getStartRow();
        return !strip.isFilled(ticketRow, column) &&
                !strip.isFilled(ticketRow + 1, column) &&
                !strip.isFilled(ticketRow + 2, column);
    }

    public int getStartRow() {
        return ticketNumber * Strip.ROWS_COUNT_PER_TICKET;
    }

    public void print() {

        int startRow = getStartRow();

        System.out.println("Ticket " + (ticketNumber + 1));
        for (int row = startRow; row < startRow + Strip.ROWS_COUNT_PER_TICKET; row++) {
            for (int column = 0; column < Strip.COLUMNS_COUNT; column++) {

                if (strip.isFilled(row, column)) {
                    System.out.printf("%2d   ", strip.getNumber(row, column));
                } else {
                    System.out.print("--   ");
                }
            }
            System.out.println();
        }
        System.out.println("\n");
    }

    public void sortColumn(int column) {
        int startRow = getStartRow();
        sortPositions(startRow, startRow + 2, column);
        sortPositions(startRow, startRow + 1, column);
        sortPositions(startRow + 1, startRow + 2, column);
    }

    private void sortPositions(int position1, int position2, int column) {
        if (strip.isFilled(position1, column) && strip.isFilled(position2, column)) {

            int number1 = strip.getNumber(position1, column);
            int number2 = strip.getNumber(position2, column);

            if (number1 > number2) {
                strip.placeNumber(position1, column, number2);
                strip.placeNumber(position2, column, number1);
            }
        }
    }
}
