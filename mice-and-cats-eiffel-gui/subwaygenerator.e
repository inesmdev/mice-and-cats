note
	description: "Summary description for {SUBWAYGENERATOR}."
	author: ""
	date: "$Date$"
	revision: "$Revision$"

class
	SUBWAYGENERATOR

inherit
	SETTINGS

create
	make

feature -- class variable
	grid : ARRAY2 [INTEGER]
	subways: LINKED_LIST [POSITION]
	goal: POSITION

feature -- Initialization
	make (a_grid: ARRAY2 [INTEGER])
		do
			grid := a_grid
			create goal.make
			create subways.make
			create_entries
		end

feature
	create_entries
		-- create random entries
		local
			rnd : RANDOMNUMBERGENERATOR
			i, col, row : INTEGER
		do
			create rnd.make
			from
				i := 1
			until
				i > number_of_subways
			loop
				col := rnd.new_random\\grid.width + 1
				row := rnd.new_random\\grid.height + 1
				grid.put (1, row, col)

				i := i + 1
			end

			goal.set_x(col)
			goal.set_y(row)

		end
feature -- return grid of subways
	get_subways: ARRAY2 [INTEGER]
		do
			Result := grid
		end
end
