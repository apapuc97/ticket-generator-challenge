# Ticket Generator Challenge

A small challenge that involves building a [Bingo 90](https://en.wikipedia.org/wiki/Bingo_(United_Kingdom)) ticket generator.

**Requirements:**

* Generate a strip of 6 tickets
    - Tickets are created as strips of 6, because this allows every number from 1 to 90 to appear across all 6 tickets. If they buy a full strip of six it means that players are guaranteed to mark off a number every time a number is called.
* A bingo ticket consists of 9 columns and 3 rows.
* Each ticket row contains five numbers and four blank spaces
* Each ticket column consists of one, two or three numbers and never three blanks.
    - The first column contains numbers from 1 to 9 (only nine),
    - The second column numbers from 10 to 19 (ten), the third, 20 to 29 and so on up until
    - The last column, which contains numbers from 80 to 90 (eleven).
* Numbers in the ticket columns are ordered from top to bottom (ASC).
* There can be **no duplicate** numbers between 1 and 90 **in the strip** (since you generate 6 tickets with 15 numbers each)

**Please make sure you add unit tests to verify the above conditions and an output to view the strips generated (command line is ok).**

Try to also think about the performance aspects of your solution. How long does it take to generate 10k strips?
The recommended time is less than 1s (with a lightweight random implementation)

# Solution

The ticket generation is done in 2 stages:
1. Generating the positions for all numbers from 1 to 90. For this, I used a recursive backtracking algorithm.
2. Filling the generated positions with random numbers (according to assignment conditions).

The backtracking algorithm takes a strip as a parameter (which is empty at the first call). It is generating and filling positions one by one at each call until all positions are filled or the current state doesn't lead to a solution. In this case, the method will go back to the previous state and will try other positions. This recursive loop will continue until a solution is found.

````java
   /**
 * Generates positions for a strip with a recursive backtracking algorithm
 *
 * @param strip the strip to be filled with generated positions
 *
 * @return true if all positions were filled. False otherwise
 */
private boolean backtrackingPositionGeneration(Strip strip) {
    //validate if the current state can lead to a solution
    if (!stripValidator.hasValidState(strip)) {
        return false;
    }

    for (int column = 0; column < Strip.COLUMNS_COUNT; column++) {
    
        if (strip.columnIsFilled(column)) {
            continue;
        }
    
        List<Integer> possiblePositions = stripValidator.getPossiblePositionsForColumn(strip, column);
        int positionsCountLeftToBeFilled = strip.getPositionsCountToBeFilledForColumn(column);
    
        Collections.shuffle(possiblePositions);
    
        for (int i = 0; i < possiblePositions.size(); i++) {
    
            if (positionsCountLeftToBeFilled > possiblePositions.size() - i) {
                return false;
            }
    
            int row = possiblePositions.get(i);
    
            strip.fillPosition(row, column);
    
            if (backtrackingPositionGeneration(strip)) {
                return true;
            }
    
            strip.undoFilling(row, column);
        }
    
        // if current column is not filled after last 'for' cycle, then we should go back to the last solution
        if (!strip.columnIsFilled(column)) {
            return false;
        }
    }
    return true;
}
````

- StripValidatorService#isValidState() method checks if the current state of the strip is a valid one. Here are done multiple checks in order to prevent generating positions further if the current state will not lead to a solution.


## Usage

```java
StripGeneratorService stripGeneratorService = new StripGeneratorService();

Strip strip = stripGeneratorService.generateStrip();

strip.print();
```

## Example of output

```bash
Ticket 1
 6   --   --   --   --   50   62   75   87   
 9   15   24   33   --   --   68   --   --   
--   --   29   37   45   --   --   76   90   


Ticket 2
--   --   --   34   --   57   63   78   80   
--   13   20   36   42   58   --   --   --   
 4   18   21   39   --   --   --   --   82   


Ticket 3
 2   11   --   30   40   52   --   --   --   
 3   --   --   --   47   53   --   70   89   
 8   17   25   --   --   --   66   79   --   


Ticket 4
 5   --   --   --   --   56   64   72   81   
--   19   27   32   41   59   --   --   --   
--   --   --   38   46   --   67   73   86   


Ticket 5
--   --   22   --   43   51   65   --   85   
 7   16   23   35   --   54   --   --   --   
--   --   28   --   44   --   69   71   88   


Ticket 6
 1   10   --   --   --   --   60   74   83   
--   12   26   31   48   55   --   --   --   
--   14   --   --   49   --   61   77   84   
```
