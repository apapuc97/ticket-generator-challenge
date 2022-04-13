package com.ticket.generator.service;

import com.ticket.generator.model.Strip;
import com.ticket.generator.model.Ticket;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class StripGeneratorServiceTest {

    /**
     * Tests that generated strip met all of the required conditions described in the assignment
     */
    @Test
    void testGeneratedStripMetAllRequiredConditions() {
        StripGeneratorService stripGeneratorService = new StripGeneratorService();
        Strip strip = stripGeneratorService.generateStrip();

        strip.print();
        assertNumbersCountPerRow(strip);
        assertNoEmptyColumnsPerTicket(strip);
        assertOrderedColumnsPerTicket(strip);
        assertAllNumbersAreFilled(strip);
    }

    /**
     * Tests that generated strip met all the conditions described in the assignment
     */
    @Test
    void testStripGenerationPerformance() {
        StripGeneratorService stripGeneratorService = new StripGeneratorService();

        Assertions.assertTimeout(Duration.ofSeconds(1), () -> {
            for (int i = 0; i < 10000; i++) {
                stripGeneratorService.generateStrip();
            }
        });
    }

    /**
     * Asserts each row has exactly {@link Strip#MAX_NUMBERS_PER_ROW} numbers
     *
     * @param strip the strip to assert
     */
    private void assertNumbersCountPerRow(Strip strip) {
        for (int row = 0; row < Strip.ROWS_COUNT; row++) {

            int numbersPerRow = 0;

            for (int column = 0; column < Strip.COLUMNS_COUNT; column++) {
                if (strip.isFilled(row, column)) {
                    numbersPerRow++;
                }
            }
            assertThat(numbersPerRow).isEqualTo(Strip.MAX_NUMBERS_PER_ROW);
        }
    }

    /**
     * Asserts all tickets has columns filled with at least one number
     *
     * @param strip the strip to assert
     */
    private void assertNoEmptyColumnsPerTicket(Strip strip) {

        for (int ticketNumber = 0; ticketNumber < Strip.TICKETS_COUNT; ticketNumber++) {

            Ticket ticket = new Ticket(strip, ticketNumber);

            for (int column = 0; column < Strip.COLUMNS_COUNT; column++) {

                assertThat(ticket.hasUnfilledColumn(column))
                        .isFalse();
            }
        }
    }

    /**
     * Asserts the numbers withing a ticket column are sorted
     *
     * @param strip the strip to assert
     */
    private void assertOrderedColumnsPerTicket(Strip strip) {

        for (int ticketNumber = 0; ticketNumber < Strip.TICKETS_COUNT; ticketNumber++) {

            Ticket ticket = new Ticket(strip, ticketNumber);

            for (int column = 0; column < Strip.COLUMNS_COUNT; column++) {

                int startRow = ticket.getStartRow();
                int finalColumn = column;

                List<Integer> filledNumbersOnColumn = IntStream.range(startRow, startRow + Strip.ROWS_COUNT_PER_TICKET)
                        .filter(row -> strip.isFilled(row, finalColumn))
                        .boxed()
                        .collect(Collectors.toList());

                assertThat(filledNumbersOnColumn).isSorted();
            }
        }
    }

    /**
     * Asserts all possible numbers in the strip are filled
     *
     * @param strip the strip to assert
     */
    private void assertAllNumbersAreFilled(Strip strip) {

        for (int column = 0; column < Strip.COLUMNS_COUNT; column++) {

            List<Integer> possibleNumbers = new LinkedList<>(strip.getPossibleNumbersPerColumn(column));

            for (int row = 0; row < Strip.ROWS_COUNT; row++) {

                if (strip.isFilled(row, column)) {

                    int number = strip.getNumber(row, column);

                    assertThat(possibleNumbers).contains(number);
                    possibleNumbers.removeIf(integer -> integer == number);
                }
            }

            assertThat(possibleNumbers).isEmpty();
        }
    }
}