note
	description: "Represents the entire game board."
	author: ""
	date: "$Date$"
	revision: "$Revision$"

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
			-- Initialize the checkerboard grid according to the settings
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
			-- helper function for defining colors more concisely
		do
			Result := create {EV_COLOR}.make_with_8_bit_rgb (r, g, b)
		end

	draw (renderer: RENDERER; super_vision: BOOLEAN)
			-- render the board using the renderer. super_vision indicates whether
			-- entities and subway tiles that are normally invisible should be rendered
			-- anyway
		local
			tile_size: INTEGER
			x, y, ox, oy: INTEGER
			curr, player_subway: INTEGER
			mu: INTEGER
		do
			renderer.grid_size (width, height)
			renderer.clear (gray)
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

			if player.is_dead then
				renderer.entity (player.pos.x, player.pos.y, color (20, 75, 134), renderer.assets.tombstone_id)
			else
				renderer.entity (player.pos.x, player.pos.y, color (20, 75, 134), renderer.assets.mouse_id)
			end

			if player_subway = entity_subway (cat) then
				renderer.entity (cat.pos.x, cat.pos.y, color (255, 127, 0), renderer.assets.cat_id)
			end

			if victory then
				renderer.center_text ("YOU WON! Press R to restart", Blue)
			elseif player.is_dead then
				renderer.center_text ("YOU DIED! Press R to restart", Red)
			end

		end

feature
	do_update
			-- moves the cat and checks if the game is over
		do
				--gives a number between 0 and 3 -> one for each possible direction
			cat.move (rand.new_random \\ 4, Current)
			check_if_dead
			check_if_won
		end

feature
	-- move entities
	move_player (dir: INTEGER)
			-- move the player entity in a given direction and checks if the game is over
		do
			if not player.is_dead then
				player.move (dir, Current)
				check_if_dead
				check_if_won
			end
		end

	entity_subway (p: PLAYER): INTEGER
			-- returns the id of the current subway or zero if the entity is not underground
		do
			if p.is_underground then
				Result := grid.item (p.pos.y, p.pos.x).abs()
			else
				Result := 0
			end
		end

	check_if_dead
			-- the player dies if it is at the same position as a cat
		do
			if cat.is_underground = player.is_underground and then
				cat.pos.is_equal (player.pos)
			then
				player.set_is_dead (TRUE)
			end
		end

	check_if_won
			-- the player wins after reaching the target subway
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
