# reactive-system

A Clojure library designed to allow you to calculate dependant values in a given row.

## Usage

1. java -jar reactive-system.jar -f FILE
    * FILE is path to a file with a CSV in which each cell is a numeric value or calculation starting with a "=".
    * Calculations are regular mathematical calculations (limited to +\-\*\/) that can reference other cells by using
        their zero-indexed number with brackets, for example {0} to reference the first cell.
2. Follow the instructions:
   1. a - print result, will print in format [INDEX: VALUE] for example "[1: 13]".
   2. b - update cell, will prompt for an index, and then prompt for the change, changes can be calculations as described
      in step 1.
   3. q - quit the app

## License

Copyright © 2020 Roldev

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
