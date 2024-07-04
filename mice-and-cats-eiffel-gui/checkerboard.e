class
	CHECKERBOARD

create
	make

feature -- Entities
	cat: CAT
	player: PLAYER

feature -- game properties
	victory: BOOLEAN
	width: INTEGER
	height: INTEGER
	rand: RANDOMNUMBERGENERATOR
	grid: ARRAY2 [INTEGER]
	subway_exits: ARRAYED_LIST [ARRAYED_LIST [POSITION]]
	goal: INTEGER

feature -- Initialization

	make (settings: SETTINGS)
			-- Initialize the checkerboard grid.
		local
			subway_generator: SUBWAYGENERATOR
		do
			create rand.make

			create subway_generator.make (settings, rand)
			grid := subway_generator.get_grid
			subway_exits := subway_generator.get_subway_exits
			goal := subway_generator.get_goal

			victory := FALSE
			width := grid.width
			height := grid.height

			create cat.make ("CAT")
			cat.set_pos (random_position())

			create player.make
			player.set_pos (random_position())
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
				y > height
			loop
				from
					x := 1
				until
					x > width
				loop
					curr := grid.item (y, x)

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
					elseif
							-- this is a subway exit
						curr < 0
					then
						pixmap.set_foreground_color (create {EV_COLOR}.make_with_rgb (0.2, 0.3, 0.3))
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

feature
	do_update
		do
				--gives a number between 0 and 3 -> one for each possible direction
			cat.move (rand.new_random \\ 4, Current)
		end

feature
	-- move entities
	move_player (dir: INTEGER)
		do
			player.move (dir, Current)
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
			if player.is_underground and then grid.item (player.pos.y, player.pos.x).abs() = goal then
				victory := true
			end
		end

feature

	random_position(): POSITION
			--set random position on the grid
		do
			create Result.make
			Result.set_x (rand.new_random \\ width + 1)
			Result.set_y (rand.new_random \\ height + 1)
		end

end
