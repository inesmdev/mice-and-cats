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
	grid: ARRAY2 [INTEGER]
	subway_exits: ARRAYED_LIST [ARRAYED_LIST [POSITION]]
	goal: INTEGER
	settings: SETTINGS

feature -- Initialization
	make (s: SETTINGS; rnd: RANDOMNUMBERGENERATOR)
		do
			settings := s
			create grid.make_filled (0, settings.game_board_height, settings.game_board_width)
			create subway_exits.make (settings.number_of_subways)
			goal := 1 -- arbitrarily pick the first subway as the goal
			create_entries (rnd)
		end

feature
	create_entries (rnd: RANDOMNUMBERGENERATOR)
			-- create random entries
		local
			i, col, row: INTEGER
		do
			from
				i := 1
			until
				i > settings.number_of_subways
			loop
				col := rnd.new_random \\ grid.width + 1
				row := rnd.new_random \\ grid.height + 1
				grid.put (1, row, col)

				i := i + 1
			end

		end
feature -- return grid of subways
	get_grid: ARRAY2 [INTEGER]
		do
			Result := grid
		end
	get_subway_exits: ARRAYED_LIST [ARRAYED_LIST [POSITION]]
		do
			Result := subway_exits
		end
	get_goal: INTEGER
		do
			Result := goal
		end

end
