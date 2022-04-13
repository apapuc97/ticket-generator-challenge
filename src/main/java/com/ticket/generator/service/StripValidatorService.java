package com.ticket.generator.service;

import com.ticket.generator.model.Strip;
import com.ticket.generator.model.Ticket;

import java.util.ArrayList;
import java.util.List;

public class StripValidatorService {


    /**
     * Checks if a position defined by row and column is a valid one and a number can be filled
     *
     * @param strip the strip to check
     * @param row the row of the position to check
     * @param column the column of the position to check
     * @return true if the position is valid and a number can be filled. False otherwise
     */
    public boolean isValidPosition(Strip strip, int row, int column) {
        return !strip.isFilled(row, column) &&
                strip.getFilledNumbersCountPerRow(row) < Strip.MAX_NUMBERS_PER_ROW &&
                strip.getFilledNumbersCountPerTicket(strip.getTicketByRow(row)) != Strip.MAX_NUMBERS_PER_TICKET;
    }

    /**
     * Checks if the current state of the strip is valid for future placements, meaning the generated
     * positions/numbers on the strip can lead to a valid strip meeting all the conditions
     *
     * @param strip the strip to be validated     *
     * @return true if the strip has a valid state. False otherwise
     */
    public boolean hasValidState(Strip strip) {
        return !checkForEmptyColumns(strip) &&
                !checkForExcessiveFilledTickets(strip) &&
                !checkForLackOfPossiblePositionsPerRow(strip) &&
                !checkForLackOfPossiblePositionsPerColumn(strip);
    }

    /**
     * Finds and returns all the possible positions within a column which can be filled with a number
     *
     * @param strip the strip to check for possible positions
     * @param column the column to be checked
     * @return a list containing all the possible positions. Empty list if there are no possible positions
     */
    public List<Integer> getPossiblePositionsForColumn(Strip strip, int column) {
        int numbersLeft = strip.getPositionsCountToBeFilledForColumn(column);
        List<Integer> possiblePositions = new ArrayList<>();
        List<Ticket> ticketsWithUnfilledColumn = strip.getTicketsWithUnfilledColumn(column);

        // if the count of numbers left is equal with unfilled tickets,
        // then only those tickets should be considered as possible positions
        if (numbersLeft == ticketsWithUnfilledColumn.size()) {

            for (Ticket ticket : ticketsWithUnfilledColumn) {
                int startRow = ticket.getStartRow();
                for (int ticketRow = startRow; ticketRow < startRow + Strip.ROWS_COUNT_PER_TICKET; ticketRow++) {
                    if (isValidPosition(strip, ticketRow, column)) {
                        possiblePositions.add(ticketRow);
                    }
                }
            }
        } else {

            for (int row = 0; row < Strip.ROWS_COUNT; row++) {
                if (isValidPosition(strip, row, column)) {
                    possiblePositions.add(row);
                }
            }
        }
        return possiblePositions;
    }

    /**
     * Check if there are tickets with unfilled columns after placing all the possible numbers/positions within a
     * column (strip.getPositionsCountToBeFilledForColumn() is empty)
     *
     * @param strip the strip to check
     * @return true if there are tickets with unfilled columns and there are no positions/numbers left for that column.
     * False otherwise.
     */
    private boolean checkForEmptyColumns(Strip strip) {
        for (int column = 0; column < Strip.COLUMNS_COUNT; column++) {

            if (strip.getPositionsCountToBeFilledForColumn(column) != 0) {
                continue;
            }

            for (int ticketNumber = 0; ticketNumber < Strip.TICKETS_COUNT; ticketNumber++) {
                Ticket ticket = new Ticket(strip, ticketNumber);

                if (ticket.hasUnfilledColumn(column)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if a ticket have more unfilled columns than numbers lef till
     * {@link Strip#MAX_NUMBERS_PER_TICKET} is reached
     *
     * @param strip the strip to be checked
     * @return true if there are excessive filled tickets
     */
    private boolean checkForExcessiveFilledTickets(Strip strip) {

        for (int ticket = 0; ticket < Strip.TICKETS_COUNT; ticket++) {
            int numbersCount = strip.getFilledNumbersCountPerTicket(ticket);
            int emptyColumns = getEmptyColumnsCountForTicket(strip, ticket);

            if (numbersCount > Strip.MAX_NUMBERS_PER_TICKET - emptyColumns) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if there are more numbers/positions left to be filled per column than actual possible positions
     *
     * @param strip the strip to be checked
     * @return true if there are more numbers/positions left than possible positions. False otherwise
     */
    private boolean checkForLackOfPossiblePositionsPerColumn(Strip strip) {
        for (int column = 0; column < Strip.COLUMNS_COUNT; column++) {

            int positionsToBeFilled = strip.getPositionsCountToBeFilledForColumn(column);

            if (positionsToBeFilled == 0) {
                continue;
            }

            int possiblePositions = getPossiblePositionsForColumn(strip, column).size();

            if (possiblePositions < positionsToBeFilled) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if there are less columns left to be filled than numbers needed to be filled per row in order
     * to achieve {@link Strip#MAX_NUMBERS_PER_ROW} numbers per row
     *
     * @param strip the strip to be checked
     * @return true if there are rows with more missing numbers than actually columns left to be filled. False otherwise
     */
    private boolean checkForLackOfPossiblePositionsPerRow(Strip strip) {

        int columnsCountLeftToBeFilled = 0;

        for (int column = 0; column < Strip.COLUMNS_COUNT; column++) {
            if (strip.getPositionsCountToBeFilledForColumn(column) != 0) {
                columnsCountLeftToBeFilled++;
            }
        }

        for (int row = 0; row < Strip.ROWS_COUNT; row++) {

            int positionsLeft = Strip.MAX_NUMBERS_PER_ROW - strip.getFilledNumbersCountPerRow(row);

            if (positionsLeft > columnsCountLeftToBeFilled) {
                return true;
            }
        }

        return false;
    }

    private int getEmptyColumnsCountForTicket(Strip strip, int ticket) {
        int emptyColumns = 0;

        for (int column = 0; column < Strip.COLUMNS_COUNT; column++) {
            if (getFilledPositionsCountForColumnAndTicket(strip, column, ticket) == 0) {
                emptyColumns++;
            }
        }
        return emptyColumns;
    }


    private int getFilledPositionsCountForColumnAndTicket(Strip strip, int column, int ticket) {
        int startRow = ticket * Strip.ROWS_COUNT_PER_TICKET;
        int numbersCount = 0;

        for (int row = startRow; row < startRow + Strip.ROWS_COUNT_PER_TICKET; row++) {
            if (strip.isFilled(row, column)) {
                numbersCount++;
            }
        }
        return numbersCount;
    }
}
