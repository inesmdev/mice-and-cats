class
	CHECKERBOARD

inherit
	SETTINGS

create
	make

feature -- Entities
	cat: CAT
	player: PLAYER
	subway_generator: SUBWAYGENERATOR
	map: ARRAY2 [INTEGER]
	subways: ARRAY2 [INTEGER]

feature -- game properties
	victory: BOOLEAN
	width: INTEGER
	height: INTEGER
	rand: RANDOMNUMBERGENERATOR

feature -- Initialization

	make
			-- Initialize the checkerboard grid.
		do
			create cat.make ("CAT")
			cat.set_pos (create {POSITION}.make)

			create rand.make

			create player.make

			create map.make_filled (0, game_board_height, game_board_width)

			create subway_generator.make (map)

			subways := map

			victory := FALSE
			width := map.width
			height := map.height
		end

	print_grid
			-- Print a 10x10 grid where each cell looks like +---+ |   | +---+
		local
			y, j, x, curr, xtmp, ytmp: INTEGER
		do

			from
				y := 1
			until
				y > map.height
			loop
					-- draw head Line
				from
					j := 1
				until
					j > map.width
				loop
					io.put_string ("+---")
					j := j + 1
				end
				io.put_string ("+%N")

					-- draw inbetween
				from
					x := 1
				until
					x > map.width
				loop
					xtmp := x
					ytmp := y --arrays start at 1
					curr := map.item (ytmp, xtmp)

					if
							-- render cat position (cat is "above" subways)
						cat.pos.x.is_equal (x) and cat.pos.y.is_equal (y)
					then
						io.put_string ("| c ")
					elseif
						player.pos.x.is_equal (x) and player.pos.y.is_equal (y)
					then
						io.put_string ("| ")
						io.put_string (player.name)
						io.putstring (" ")
					elseif
							-- this is a subway cell
						curr > 0
					then
						io.put_string ("| O ")
					else
							-- this is a normal, empty cell
						io.put_string ("|   ")
					end
					x := x + 1
				end
				io.put_string ("|%N")
				y := y + 1

			end

				-- draw bottom line
			from
				j := 1
			until
				j > map.width
			loop
				io.put_string ("+---")
				j := j + 1
			end
			io.put_string ("+%N")
		end

	draw (w, h: INTEGER; pixmap: EV_PIXMAP)
		local
			tile_size: INTEGER
			x, y, ox, oy: INTEGER
			curr: INTEGER
		do
			tile_size := (w // width).min (h // height)
			ox := (w - (width * tile_size)) // 2
			oy := (h - (height * tile_size)) // 2

			from
				y := 1
			until
				y > map.height
			loop
				from
					x := 1
				until
					x > map.width
				loop
					curr := map.item (y, x)

					if
							-- render cat position (cat is "above" subways)
						cat.pos.x.is_equal (x) and cat.pos.y.is_equal (y)
					then
						pixmap.set_foreground_color (create {EV_COLOR}.make_with_rgb (0, 0, 1))
					elseif
						player.pos.x.is_equal (x) and player.pos.y.is_equal (y)
					then
						pixmap.set_foreground_color (create {EV_COLOR}.make_with_rgb (0, 1, 0))
					elseif
							-- this is a subway cell
						curr > 0
					then
						pixmap.set_foreground_color (create {EV_COLOR}.make_with_rgb (0, 0, 0))
					else
							-- this is a normal, empty cell
						if (x + y) \\ 2 = 0 then
							pixmap.set_foreground_color (create {EV_COLOR}.make_with_rgb (1, 0.5, 0.5))
						else
							pixmap.set_foreground_color (create {EV_COLOR}.make_with_rgb (1, 0.8, 0.8))
						end
					end

					pixmap.fill_rectangle (ox + (x - 1) * tile_size, oy + (y - 1) * tile_size, tile_size, tile_size)

					x := x + 1
				end
				y := y + 1

			end

		end

feature -- change positions of entities
	print_frame
		local
			random: RANDOMNUMBERGENERATOR
		do
				-- move cat
			create random.make
				--gives a number between 0 and 3 -> one for each possible direction
			cat.move (random.new_random \\ 10 \\ 4)
			print_grid
		end

feature
	do_update
		do
				--gives a number between 0 and 3 -> one for each possible direction
			cat.move (rand.new_random \\ 4)
		end

feature
	-- move entities
	move_player (key: CHARACTER)
		do
			player.move (player.char_to_move (key))
		end

feature

	check_if_dead
		do
			if
				cat.pos.equals (player.pos)
			then
				player.set_is_dead (TRUE)
			end
		end

	check_if_won
		do
			if player.pos.x.is_equal (subway_generator.goal.x) and player.pos.y.is_equal (subway_generator.goal.y) then
				victory := true
			end
		end

end
