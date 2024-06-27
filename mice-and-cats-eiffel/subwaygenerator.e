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
		ensure
			check_at_least_one_exit(grid)
		end

	check_at_least_one_exit (array: ARRAY2 [INTEGER]): BOOLEAN
            -- Check if all elements in `a_array` are zero.
        local
            i, j, sum: INTEGER
        do
        	sum := 0
            from
                i := 1
            until
                i >= 10
            loop
                from
                    j := 1
                until
                    j >= 10
                loop
                	sum := sum + array.item(i,j)
                    j := j + 1
                end
                i := i + 1
            end
            Result := sum > 0
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
