note
	description: "Summary description for {SUBWAYGENERATOR}."
	author: ""
	date: "$Date$"
	revision: "$Revision$"

class
	SUBWAYGENERATOR

create
	make

feature -- class variable
	grid : ARRAY2 [INTEGER]

feature -- Initialization
	make
		do
			create grid.make_filled (0, 10, 10)
			create_subways
		end

feature -- create 4 random non-overlapping subways

    dead_code
            -- Place four horizontal lines on the grid, each at least one row apart and at least 4 cells long.
        local
            row, col, i, first, last, random_int: INTEGER
            random : RANDOMNUMBERGENERATOR
        do
        	create random.make
			random_int := random.get_random
            from
                row := 1
                i := 1
            until
                i > 4
            loop
            	first := random_int\\10 --last digit
            	last := random_int//10 --second lasat digit
            	if
            		first > 6
            	then
            		first := first - 3 -- ensure that subway can be 4 cells long
            	end

            	if
            		last < 4
            	then
            		last := last + 3 -- ensure that subway can be 4 cells long
            	end
                from
                    col := 0
                until
                    col > 10
                loop
                    grid.put (i, row, col)
                    col := col + 1
                end
                row := row + 2 -- Ensure the next line is at least one row apart
                i := i + 1
            end
        end


    create_subways
            -- Place four horizontal lines on the grid, each at least one row apart.
        local
            row, col, i: INTEGER
        do
            from
                row := 1
                i := 1
            until
                i > 4
            loop
                from
                    col := 1
                until
                    col > 10
                loop
                    grid.put (row, row, col)
                    col := col + 1
                end
                row := row + 2 -- Ensure the next line is at least one row apart
                i := i + 1
            end
        end

feature -- return grid of subways
	get_subways: ARRAY2 [INTEGER]
		do
			Result := grid
		end
end
