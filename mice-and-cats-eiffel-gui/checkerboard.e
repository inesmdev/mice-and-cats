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
	subway_colors: ARRAYED_LIST [EV_COLOR]
	super_vision: BOOLEAN

feature -- Initialization

	make (settings: SETTINGS)
			-- Initialize the checkerboard grid.
		require
			enough_colors_for_subways: settings.number_of_subways <= 10
		local
			subway_generator: SUBWAYGENERATOR
		do
			create rand.make

			create subway_colors.make (10)
			subway_colors.extend (color (255, 99, 71))
			subway_colors.extend (color (135, 206, 235))
			subway_colors.extend (color (255, 182, 193))
			subway_colors.extend (color (144, 238, 144))
			subway_colors.extend (color (255, 165, 0))
			subway_colors.extend (color (173, 216, 230))
			subway_colors.extend (color (238, 130, 238))
			subway_colors.extend (color (240, 128, 128))
			subway_colors.extend (color (221, 160, 221))
			subway_colors.extend (color (32, 178, 170))
			super_vision := FALSE

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

	color (r, g, b: INTEGER): EV_COLOR
		do
			Result := create {EV_COLOR}.make_with_8_bit_rgb (r, g, b)
		end

	draw (w, h: INTEGER; pixmap: EV_PIXMAP)
		local
			tile_size: INTEGER
			x, y, ox, oy: INTEGER
			curr, player_subway: INTEGER
			mu: INTEGER
		do
			tile_size := (w // width).min (h // height)
			ox := (w - (width * tile_size)) // 2
			oy := (h - (height * tile_size)) // 2
			if player.is_underground then
				player_subway := grid.item (player.pos.y, player.pos.x).abs()
			else
				player_subway := 0
			end
			mu := tile_size // 20

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

					if player_subway = 0 then
						if curr < 0 then
							pixmap.set_foreground_color (subway_colors @ - curr)
						elseif super_vision and then curr > 0 then
							pixmap.set_foreground_color (subway_colors @ curr)
						else
							pixmap.set_foreground_color (create {EV_COLOR}.make_with_rgb (1, 1, 1))
						end
					else
						if curr = - player_subway then
							pixmap.set_foreground_color (create {EV_COLOR}.make_with_rgb (1, 1, 1))
						elseif curr = player_subway then
							pixmap.set_foreground_color (subway_colors @ player_subway)
						elseif super_vision and then curr /= 0 then
							pixmap.set_foreground_color (subway_colors @ curr.abs())
						else
							pixmap.set_foreground_color (create {EV_COLOR}.make_with_rgb (0, 0, 0))
						end
					end
					pixmap.fill_rectangle (ox + (x - 1) * tile_size, oy + (y - 1) * tile_size, tile_size, tile_size)

						--pixmap.set_foreground_color (create {EV_COLOR}.make_with_rgb (0, 0, 0))
						--pixmap.draw_rectangle (ox + (x - 1) * tile_size, oy + (y - 1) * tile_size, tile_size, tile_size)
					x := x + 1
				end
				y := y + 1

			end

			pixmap.set_foreground_color (create {EV_COLOR}.make_with_rgb (0.67, 0.84, 0.9))
			pixmap.fill_rectangle (ox + (player.pos.x - 1) * tile_size + mu, oy + (player.pos.y - 1) * tile_size + mu, tile_size - 2 * mu, tile_size - 2 * mu)
			pixmap.set_foreground_color (create {EV_COLOR}.make_with_rgb (0, 0, 0))
			pixmap.draw_rectangle (ox + (player.pos.x - 1) * tile_size + mu, oy + (player.pos.y - 1) * tile_size + mu, tile_size - 2 * mu, tile_size - 2 * mu)

			if cat.is_underground = player.is_underground then
				pixmap.set_foreground_color (create {EV_COLOR}.make_with_rgb (1, 0.5, 0))
				pixmap.fill_ellipse (ox + (cat.pos.x - 1) * tile_size + mu, oy + (cat.pos.y - 1) * tile_size + mu, tile_size - 2 * mu, tile_size - 2 * mu)
				pixmap.set_foreground_color (create {EV_COLOR}.make_with_rgb (0, 0, 0))
				pixmap.draw_ellipse (ox + (cat.pos.x - 1) * tile_size + mu, oy + (cat.pos.y - 1) * tile_size + mu, tile_size - 2 * mu, tile_size - 2 * mu)
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

	toggle_super_vision
		do
			super_vision := not super_vision
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
