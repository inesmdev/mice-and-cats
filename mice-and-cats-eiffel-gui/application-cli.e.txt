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
            j: INTEGER
            key : CHARACTER
        do
            create checkerboard.make
            io.putstring ("NEW GAME%N")
            print_usage

            checkerboard.print_grid
            from
            	i := 0
            until
            	i >= 20 or else checkerboard.player.is_dead or else key = 'q' or else checkerboard.victory
            loop
            	io.put_string ("Please enter key: %N")
            	io.put_string ("MOVES left")
            	io.put_integer (20 - i)
            	io.put_string ("%N")
            	key := read_char

            	print_usage
            	io.new_line

            	checkerboard.move_player(key)
            	checkerboard.print_frame
            	checkerboard.check_if_dead
            	checkerboard.check_if_won
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

feature

	read_char: CHARACTER
        -- Read a character from a console without waiting for Enter.
    	external "C inline use <conio.h>"
        	alias "return getch ();"
    	end

feature {NONE}

	print_usage
		do
			io.put_string ("Press keys (press 'q' to quit):%N")
            io.put_string ("Press 'a' to move left. %N")
            io.put_string ("Press 'w' to move up. %N")
            io.put_string ("Press 's' to move down. %N")
            io.put_string ("Press 'd' to move right. %N")
		end

end
