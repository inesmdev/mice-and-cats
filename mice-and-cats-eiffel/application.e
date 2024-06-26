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
        do
            create checkerboard.make
            io.putstring ("NEW GAME")
            io.new_line
            checkerboard.print_grid
            from
            	i := 0
            until
            	i >= 10 or else checkerboard.player.is_dead
            loop
            	checkerboard.print_frame
            	io.new_line
            	i := i + 1
            end

            if
            	checkerboard.player.is_dead = TRUE
            then
            	io.put_boolean (checkerboard.player.is_dead)
            	io.put_string ("GAME OVER: YOU DIED.")
            	io.new_line
            elseif
            	i = 10
            then
            	io.put_string ("GAME OVER: COULD NOT HIDE IN TIME.")
            	io.new_line
            end

        end

end
