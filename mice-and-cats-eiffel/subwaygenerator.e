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
				i = 4
			loop
				col := rnd.new_random\\10 + 1
				row := rnd.new_random\\10 + 1
				grid.put (1, row, col)

				i := i + 1
			end

		end
feature -- return grid of subways
	get_subways: ARRAY2 [INTEGER]
		do
			Result := grid
		end
end
