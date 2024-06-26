note
	description: "cli-cat-mouse application root class"
	date: "$Date$"
	revision: "$Revision$"

class
	APPLICATION

inherit
	ARGUMENTS_32

create
	make

feature {NONE} -- Initialization

    make
            -- Initialize the main application.
        local
            checkerboard: CHECKERBOARD
            i : INTEGER
            key : CHARACTER
        do
            create checkerboard.make
            io.putstring ("NEW GAME%N")
            io.put_string ("Press keys (press 'q' to quit):%N")
            io.put_string ("Press 'a' to move left. %N")
            io.put_string ("Press 'w' to move up. %N")
            io.put_string ("Press 's' to move down. %N")
            io.put_string ("Press 'd' to move right. %N")

            checkerboard.print_grid
            from
            	i := 0
            until
            	i >= 20 or else checkerboard.player.is_dead or else key = 'q' or else checkerboard.victory
            loop
            	io.put_string ("Please enter key: %N")
            	key := io.last_character
            	io.read_character -- needed for blocking
            	checkerboard.move_player(key)
            	checkerboard.print_frame
            	io.new_line
            	i := i + 1
            end

            if
            	checkerboard.player.is_dead = TRUE
            then
            	io.put_string ("GAME OVER: YOU DIED.")
            	io.new_line
            elseif checkerboard.victory then
            	io.putstring ("YOU HAVE WON.")

            elseif
            	i = 20
            then
            	io.put_string ("GAME OVER: COULD NOT HIDE IN TIME.")
            	io.new_line
            elseif
            	key = 'q'
            then
            	io.put_string ("Player has quit the game.%N")
            end

        end

end
