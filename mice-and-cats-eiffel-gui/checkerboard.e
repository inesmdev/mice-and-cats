class
	CHECKERBOARD

inherit
	EV_STOCK_COLORS

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

	draw (renderer: RENDERER; super_vision: BOOLEAN)
		local
			tile_size: INTEGER
			x, y, ox, oy: INTEGER
			curr, player_subway: INTEGER
			mu: INTEGER
		do
			renderer.grid_size (width, height)
			player_subway := entity_subway (player)

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
							renderer.tile (x, y, Black)
							renderer.tile_center (x, y, subway_colors @ - curr)
						elseif super_vision and then curr > 0 then
							renderer.tile (x, y, subway_colors @ curr)
						else
							renderer.tile (x, y, White)
						end
					else
						if curr = - player_subway or else (curr < 0 and super_vision) then
							renderer.tile (x, y, White)
							renderer.tile_center (x, y, subway_colors @ - curr)
						elseif curr = player_subway or else (curr > 0 and super_vision) then
							renderer.tile (x, y, subway_colors @ curr)
						else
							renderer.tile (x, y, Black)
						end
					end

					x := x + 1
				end
				y := y + 1
			end

			renderer.entity (player.pos.x, player.pos.y, color (20, 75, 134))

			if player_subway = entity_subway (cat) then
				renderer.entity (cat.pos.x, cat.pos.y, color (255, 127, 0))
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

	entity_subway (p: PLAYER): INTEGER
		do
			if p.is_underground then
				Result := grid.item (p.pos.y, p.pos.x).abs()
			else
				Result := 0
			end
		end

	check_if_dead
		do
			if cat.is_underground = player.is_underground and then
				cat.pos.equals (player.pos)
			then
				player.set_is_dead (TRUE)
			end
		end

	check_if_won
		do
			if entity_subway (player) = goal then
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
