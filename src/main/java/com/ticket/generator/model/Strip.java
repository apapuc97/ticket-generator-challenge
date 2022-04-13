package com.ticket.generator.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Strip {
    public static final int ROWS_COUNT = 18;
    public static final int COLUMNS_COUNT = 9;
    public static final int TICKETS_COUNT = 6;
    public static final int ROWS_COUNT_PER_TICKET = 3;
    public static final int MAX_NUMBERS_PER_TICKET = 15;
    public static final int MAX_NUMBERS_PER_ROW = 5;
    public static final int FILLED_POSITION_FLAG = 1;
    public static final int UNFILLED_POSITION = -1;

    private final int[][] tickets;
    private final int[] filledNumbersCountPerTicket;
    private final int[] filledNumbersCountPerRow;

    private final List<List<Integer>> possibleNumbersPerColumn;
    private final int[] positionsCountLeftToBeFilledPerColumn;


    public Strip() {
        tickets = new int[ROWS_COUNT][COLUMNS_COUNT];
        filledNumbersCountPerTicket = new int[TICKETS_COUNT];
        filledNumbersCountPerRow = new int[ROWS_COUNT];

        for (int i = 0; i < ROWS_COUNT; i++) {
            for (int j = 0; j < COLUMNS_COUNT; j++) {
                tickets[i][j] = -1;
            }
        }
        possibleNumbersPerColumn = new ArrayList<>(COLUMNS_COUNT);

        initPossibleValues();

        positionsCountLeftToBeFilledPerColumn = new int[COLUMNS_COUNT];

        for (int column = 0; column < COLUMNS_COUNT; column++) {
            positionsCountLeftToBeFilledPerColumn[column] = possibleNumbersPerColumn.get(column).size();
        }
    }

    public void fillPosition(int row, int column) {
        tickets[row][column] = FILLED_POSITION_FLAG;
        positionsCountLeftToBeFilledPerColumn[column]--;
        filledNumbersCountPerTicket[getTicketByRow(row)]++;
        filledNumbersCountPerRow[row]++;
    }

    public void placeNumber(int row, int column, int number) {
        tickets[row][column] = number;
    }

    public void undoFilling(int row, int column) {
        tickets[row][column] = -1;

        positionsCountLeftToBeFilledPerColumn[column]++;
        filledNumbersCountPerTicket[getTicketByRow(row)]--;
        filledNumbersCountPerRow[row]--;
    }

    public int getPositionsCountToBeFilledForColumn(int column) {
        return positionsCountLeftToBeFilledPerColumn[column];
    }

    public int getTicketByRow(int row) {
        return row / 3;
    }

    public boolean isFilled(int row, int column) {
        return tickets[row][column] != UNFILLED_POSITION;
    }

    public boolean columnIsFilled(int column) {
        return positionsCountLeftToBeFilledPerColumn[column] == 0;
    }

    public void print() {
        for (int ticket = 0; ticket < TICKETS_COUNT; ticket++) {
            new Ticket(this, ticket).print();
        }
    }

    public int getNumber(int row, int column) {
        return tickets[row][column];
    }

    public int getFilledNumbersCountPerTicket(int ticket) {
        return filledNumbersCountPerTicket[ticket];
    }

    public int getFilledNumbersCountPerRow(int row) {
        return filledNumbersCountPerRow[row];
    }

    public List<Integer> getPossibleNumbersPerColumn(int column) {
        return possibleNumbersPerColumn.get(column);
    }

    public List<Ticket> getTicketsWithUnfilledColumn(int column) {

        List<Ticket> ticketsWithUnfilledColumn = new ArrayList<>();

        for (int ticketNumber = 0; ticketNumber < Strip.TICKETS_COUNT; ticketNumber++) {

            Ticket ticket = new Ticket(this, ticketNumber);

            if (ticket.hasUnfilledColumn(column)) {
                ticketsWithUnfilledColumn.add(ticket);
            }
        }
        return ticketsWithUnfilledColumn;
    }

    private void initPossibleValues() {
        for (int column = 0; column < COLUMNS_COUNT; column++) {
            List<Integer> possibleValues = new LinkedList<>();

            for (int i = 0; i <= 10; i++) {
                if ((column == 0 && (i == 0 || i == 10)) ||
                        (column != COLUMNS_COUNT - 1 && i == 10)) {
                    continue;
                }

                possibleValues.add(column * 10 + i);
            }
            possibleNumbersPerColumn.add(possibleValues);
        }
    }

    public void sortColumnsPerTicket() {
        for (int ticketNumber = 0; ticketNumber < TICKETS_COUNT; ticketNumber++) {
            Ticket ticket = new Ticket(this, ticketNumber);

            for (int column = 0; column < COLUMNS_COUNT; column++) {
                ticket.sortColumn(column);
            }
        }
    }


}
