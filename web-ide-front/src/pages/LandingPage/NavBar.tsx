import { Button, ButtonGroup, Flex, Image } from "@chakra-ui/react"
import logo from '../../assets/images/logo.png';
import { useNavigate } from "react-router-dom"

const NavBar: React.FC = () => {
	const navigate = useNavigate();
	const onClickButton = (path: string) => () => {
		navigate(path);
	}

	return (
		<Flex align='center' justifyContent='space-between' w='100%' p='16px'>
			<Image
				w='120px'
				objectFit='cover'
				src={logo}
				alt='3Ever logo image'
			/>
			<ButtonGroup>
				<Button onClick={onClickButton('/signup')} colorScheme='green' variant='solid'>Sign Up</Button>
				<Button onClick={onClickButton('/login')} colorScheme='green' variant='outline'>Login</Button>
			</ButtonGroup>
		</Flex>
	)
}

export default NavBar
