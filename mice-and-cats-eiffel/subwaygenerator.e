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
    create_subways
            -- Place four horizontal lines on the grid, each at least one row apart.
        local
            row, col, i: INTEGER
        do
            from
                row := 1
                i := 1
            until
                i > 4 or else row > 10
            loop
                from
                    col := 1
                until
                    col > 10
                loop
                    grid.put (row, row, col)
                    col := col + 4
                end
                row := row + 4 -- Ensure the next line is at least one row apart
                i := i + 1
            end
        end

feature -- return grid of subways
	get_subways: ARRAY2 [INTEGER]
		do
			Result := grid
		end
end
